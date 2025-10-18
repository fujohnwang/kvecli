package com.keevol.kvectors.cli.commands

import com.keevol.kvectors.KVectors
import com.keevol.kvectors.cli.TerminalAttached
import com.keevol.kvectors.enums.{CompressionStrategy, IndexStrategy}
import org.apache.commons.lang3.{StringUtils, Strings}
import picocli.CommandLine.{Command, Parameters, Option => CliOption}


@Command(name = "create",
  aliases = Array("new", "touch", "spawn"),
  mixinStandardHelpOptions = true,
  description = Array("create a new kvectors collection to store vectors"))
class CreateCollectionCommand(kdb: KVectors) extends Runnable with TerminalAttached {

  @CliOption(
    names = Array("-i", "--index"),
    required = false,
    arity = "0..1",
    fallbackValue = "I", // 关键：如果用户只输入了 -i 而没有给值，strategy 将被设为这个特殊值
    description = Array("index strategy to use with the collection")
  )
  var indexStrategy: Option[String] = None

  @CliOption(
    names = Array("-c", "--compress"),
    required = false,
    arity = "0..1",
    fallbackValue = "CLI_OPTION_ENABLED_WITHOUT_VALUE_PROVIDED", // 关键：如果用户只输入了 -c 而没有给值，strategy 将被设为这个特殊值
    description = Array("index strategy to use with the collection")
  )
  var compressionStrategy: Option[String] = None

  @CliOption(
    names = Array("--memory"),
    // 对于 boolean 类型的字段，不需要写 arity，Picocli 会自动处理
    // required = false 是默认值
    description = Array("whether to store vectors in memory only.")
  )
  var inMemoryCollection: Boolean = false

  @Parameters(description = Array("the collection name to create with"))
  var name: String = _

  override def run(): Unit = {
    if (StringUtils.isEmpty(name)) {
      echo("you must provide a collection name to create with")
      return
    }
    val idxStrategy = indexStrategy match {
      case None => IndexStrategy.NO_INDEX
      case Some(str) => if (Strings.CI.equalsAny(indexStrategy.get, "no", "no_index", "no-index")) IndexStrategy.NO_INDEX else if (Strings.CI.equalsAny(indexStrategy.get, "ann")) IndexStrategy.ANN else IndexStrategy.NO_INDEX
    }
    val compress = compressionStrategy match {
      case None => CompressionStrategy.NO
      case Some("CLI_OPTION_ENABLED_WITHOUT_VALUE_PROVIDED") => CompressionStrategy.ZSTD
      case Some(str) => CompressionStrategy.valueOf(str)
    }

    kdb.createVectorCollection(name, idxStrategy, compress, inMemory = inMemoryCollection)

    echo(s"collection created successfully: ${name}")
  }

}