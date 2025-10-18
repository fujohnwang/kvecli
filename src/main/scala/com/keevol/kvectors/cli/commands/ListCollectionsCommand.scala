package com.keevol.kvectors.cli.commands

import com.keevol.kvectors.KVectors
import picocli.CommandLine.Command

@Command(name = "list", mixinStandardHelpOptions = true,
  description = Array("list kvectors' collections on this node."))
class ListCollectionsCommand(kdb: KVectors) extends Runnable {
  override def run(): Unit = println(kdb.listCollections().mkString("\n"))
}
