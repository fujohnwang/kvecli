package com.keevol.kvectors.cli

/**
 * <pre>
 * :::    ::: :::::::::: :::::::::: :::     :::  ::::::::  :::
 * :+:   :+:  :+:        :+:        :+:     :+: :+:    :+: :+:
 * +:+  +:+   +:+        +:+        +:+     +:+ +:+    +:+ +:+
 * +#++:++    +#++:++#   +#++:++#   +#+     +:+ +#+    +:+ +#+
 * +#+  +#+   +#+        +#+         +#+   +#+  +#+    +#+ +#+
 * #+#   #+#  #+#        #+#          #+#+#+#   #+#    #+# #+#
 * ###    ### ########## ##########     ###      ########  ##########
 * </pre>
 * <p>
 * KEEp eVOLution!
 * <p>
 *
 * @author fq@keevol.cn
 * @since 2017.5.12
 * <p>
 * Copyright 2017 © 杭州福强科技有限公司版权所有 (<a href="https://www.keevol.cn">keevol.cn</a>)
 */

import com.keevol.kvectors.KVectors
import com.keevol.kvectors.cli.commands.ai.{CompletionCommand, EmbeddingCommand}
import com.keevol.kvectors.cli.commands.{AddVectorCommand, CreateCollectionCommand, DropCollectionCommand, HelpCommand, ListCollectionsCommand, LongRunCommand, SimilaritySearchCommand}
import com.keevol.kvectors.utils.{Closables, Http}
import io.vertx.core.json.{JsonArray, JsonObject}
import org.apache.commons.lang3.StringUtils
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.{Terminal, TerminalBuilder}
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.{Command, IFactory, Option => CliOption}
import picocli.shell.jline3.PicocliJLineCompleter

import java.net.URI
import java.util.concurrent.atomic.AtomicBoolean
import collection.JavaConverters._


@Command(name = "kvecli",
  mixinStandardHelpOptions = true, // 自动添加 --help 和 --version 选项
  description = Array("An interactive shell for KVectors"),
  subcommands = Array(
    classOf[HelpCommand],
    classOf[ListCollectionsCommand],
    classOf[LongRunCommand],
    classOf[DropCollectionCommand],
    classOf[CreateCollectionCommand],
    classOf[AddVectorCommand],
    classOf[SimilaritySearchCommand],
    classOf[EmbeddingCommand],
    classOf[CompletionCommand]
  )
) // 注册子命令
class Kvecli extends Runnable {
  // 使用 @Option 注解定义命令行选项
  // 'interactive = true' 表示如果命令行没有提供此选项，则会提示用户输入
  // 'description' 用于在 --help 输出中显示
  @CliOption(
    names = Array("-u", "--user"),
    description = Array("用户名。"),
    //    required = true,      // 告诉 Picocli 这个选项是必须的
    interactive = true,
    prompt = "请输入用户名: " // JLine 提示符
  )
  var username: String = _ // 将由 Picocli 填充

  @CliOption(
    names = Array("-p", "--password"),
    description = Array("密码。"),
    //    required = true,      // 告诉 Picocli 这个选项是必须的
    interactive = true,
    prompt = "请输入密码: ", // JLine 提示符
    arity = "0..1", // 允许密码为空
    echo = false // 设置为 false 来隐藏输入
  )
  var password: Array[Char] = _ // 使用 Char 数组存储密码更安全

  /**
   * IntelliJ IDEA这种IDE里运行交互的shell有问题，它没有办法提供完全的interactive shell
   *
   * 所以， 用户名和密码验证这种交互在IDE里跑就有问题，所以，暂时disable。
   *
   * 如果有需要真正的cli client，到时候打包成一个独立的fatjar交付，然后在正常的terminal/iterm2里跑就行了。
   */
  override def run(): Unit = {
    System.out.println(
      """Welcome to the interactive shell of KVector.
        |
        | ██╗  ██╗ ██╗   ██╗ ███████╗  ██████╗ ██╗      ██╗
        | ██║ ██╔╝ ██║   ██║ ██╔════╝ ██╔════╝ ██║      ██║
        | █████╔╝  ██║   ██║ █████╗   ██║      ██║      ██║
        | ██╔═██╗  ╚██╗ ██╔╝ ██╔══╝   ██║      ██║      ██║
        | ██║  ██╗  ╚████╔╝  ███████╗ ╚██████╗ ███████╗ ██║
        | ╚═╝  ╚═╝   ╚═══╝   ╚══════╝  ╚═════╝ ╚══════╝ ╚═╝
        |""".stripMargin)

    //    println("正在尝试进行身份验证...")
    //
    //    if (password == null || StringUtils.isAnyEmpty(username, new String(password))) {
    //      throw new IllegalAccessException("illegal username or password. run `kvecli --help` to learn more.")
    //    }
    //    println("\n\u001B[32m认证成功！\u001B[0m") // 绿色成功信息
    //    println(s"欢迎, $username!")
  }
}


/**
 * 后面可以用Spring Shell处理类似场景
 *
 * @param terminal
 * @param kdb
 */
class CommandLineFactory(terminal: Terminal, kdb: KVectors) extends IFactory {

  override def create[K](clazz: Class[K]): K = {
    if (clazz == classOf[ListCollectionsCommand]) {
      return new ListCollectionsCommand(kdb).asInstanceOf[K]
    }
    if (clazz == classOf[DropCollectionCommand]) {
      return new DropCollectionCommand(kdb).asInstanceOf[K]
    }
    if (clazz == classOf[CreateCollectionCommand]) {
      return new CreateCollectionCommand(kdb).asInstanceOf[K]
    }
    if (clazz == classOf[AddVectorCommand]) {
      return new AddVectorCommand(kdb).asInstanceOf[K]
    }
    if (clazz == classOf[SimilaritySearchCommand]) {
      return new SimilaritySearchCommand(kdb).asInstanceOf[K]
    }

    val cmd = CommandLine.defaultFactory().create(clazz)
    if (cmd.isInstanceOf[TerminalAttached]) {
      cmd.asInstanceOf[TerminalAttached].terminal = terminal
    }
    cmd
  }
}


/**
 * This is a REPL CLI demo which mimics customer service chat via app or web.
 *
 * It's main purpose is to demonstrate how the embedding and KVectors work together.
 */
object Kvecli {
  private val logger = LoggerFactory.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {
    val kdb = new KVectors()
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = Closables.closeWithLog(kdb)
    })

    val terminal = TerminalBuilder.builder().system(true).build();
    val cmd = new CommandLine(new Kvecli(), new CommandLineFactory(terminal, kdb))
    val lineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .completer(new PicocliJLineCompleter(cmd.getCommandSpec)) // **关键：设置 Picocli 自动补全**
      .build();

    try {
      val exitCode = cmd.execute(args: _*); // 执行父命令的 run 方法，打印欢迎信息
      if (exitCode != 0) {
        System.exit(exitCode)
      }
    } catch {
      case t: Throwable => {
        System.exit(1)
      }
    }

    //    // *** 如果认证成功，才进入交互式 Shell ***
    //    println("\n认证成功，进入交互式 Shell。输入 'exit' 或 'quit' 退出。")

    val prompt = "kvecli> ";
    val running = new AtomicBoolean(true)
    while (running.get) {
      val line = lineReader.readLine(prompt);
      if (StringUtils.isNotEmpty(line)) {
        if ("exit".equalsIgnoreCase(line.trim()) || "quit".equalsIgnoreCase(line.trim())) {
          running.set(false)
        } else {
          // 解析并执行用户输入的命令
          // CommandLine.execute 会自动处理输入并调用相应的 run 方法
          val parsedLine = lineReader.getParser.parse(line, 0) // 0 表示从光标位置0开始解析, 使用parser也是为了正确处理参数用引号包裹的情况。
          val words = parsedLine.words().asScala.toArray
          cmd.execute(words: _*);
        }
      }
    }
    terminal.writer().println("bye~ have a good day!")
    terminal.flush()

  }
}