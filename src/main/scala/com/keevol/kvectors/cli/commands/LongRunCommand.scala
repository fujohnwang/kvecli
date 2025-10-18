package com.keevol.kvectors.cli.commands

import com.keevol.kvectors.cli.TerminalAttached
import picocli.CommandLine.Command

import java.util.concurrent.TimeUnit

@Command(name = "longrun", mixinStandardHelpOptions = true,
  description = Array("demonstrate status and progress update with long run task."))
class LongRunCommand extends Runnable with TerminalAttached{

  override def run(): Unit = {
    runWithSpinner {
      TimeUnit.SECONDS.sleep(10)
    }
  }
}