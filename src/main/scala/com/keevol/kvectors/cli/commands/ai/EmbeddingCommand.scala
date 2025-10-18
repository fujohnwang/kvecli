package com.keevol.kvectors.cli.commands.ai

import com.keevol.kvectors.cli.TerminalAttached
import com.keevol.kvectors.cli.utils.Ollama
import org.apache.commons.lang3.StringUtils
import picocli.CommandLine.{Command, Parameters}

@Command(name = "embed",
  aliases = Array("embedding", "encode"),
  mixinStandardHelpOptions = true,
  description = Array("generate embedding vector as per NL message."))
class EmbeddingCommand extends Runnable with TerminalAttached {

  @Parameters(index = "0", arity = "1", description = Array("the message to be embedded with model"))
  var message: String = _

  override def run(): Unit = {
    if (StringUtils.isEmpty(message)) {
      echo("you must provide a message to run against embedding model")
      return
    }
    val result = Ollama.embedding(message)
    echo(result)
  }
}
