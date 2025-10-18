package com.keevol.kvectors.cli.commands

import com.keevol.kvectors.KVectors
import com.keevol.kvectors.cli.TerminalAttached
import org.apache.commons.lang3.StringUtils
import picocli.CommandLine.{Command, Parameters}

@Command(name = "drop", mixinStandardHelpOptions = true,
  description = Array("NOTE: this is a dangerous operation, perform it at your own cost."))
class DropCollectionCommand(kdb: KVectors) extends Runnable with TerminalAttached {

  @Parameters(description = Array("collection name to drop"))
  var collectionName: String = _

  override def run(): Unit = {
    if (StringUtils.isEmpty(collectionName)) {
      echo("collection name must be provided!")
      return
    }
    kdb.dropCollection(collectionName)
    echo(s"collection:${collectionName} is dropped.")
  }
}