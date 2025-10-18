package com.keevol.kvectors.cli.commands

import picocli.CommandLine
import picocli.CommandLine.{Command, Parameters, Spec}
import picocli.CommandLine.Model.CommandSpec

@Command(
  name = "help",
  description = Array("显示关于命令的帮助信息。")
)
class HelpCommand extends Runnable {

  @Spec
  var spec: CommandSpec = _

  @Parameters(
    arity = "0..1", // 可以接受0个或1个参数
    description = Array("需要获取帮助的命令名称。")
  )
  var commandName: String = _

  override def run(): Unit = {
    val parentCommandLine: CommandLine = spec.parent().commandLine()

    if (commandName == null) {
      // 1. 如果用户只输入了 "help"
      // 打印父命令（即 KVecli）的帮助信息
      parentCommandLine.usage(System.out)
    } else {
      // 2. 如果用户输入了 "help <command>"
      val subcommand: CommandLine = parentCommandLine.getSubcommands.get(commandName)
      if (subcommand == null) {
        // 如果找不到这个子命令
        System.err.println(s"错误: 未知的命令 '$commandName'")
        System.err.println(s"运行 'help' 来查看所有可用命令。")
      } else {
        // 打印指定子命令的帮助信息
        subcommand.usage(System.out)
      }
    }
  }
}