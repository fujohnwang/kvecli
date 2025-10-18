package com.keevol.kvectors.cli

import org.jline.terminal.Terminal
import org.jline.utils.Status

import java.util.concurrent.atomic.AtomicReference


trait TerminalAttached {
  var terminal: Terminal = _

  def runWithSpinner[T](r: => T): T = {
    val status = Status.getStatus(terminal)
    val result = new AtomicReference[T]()
    try {
      result.set(r)
    } finally {
      status.close()
    }
    echo("done!")
    result.get()
  }

  def echo(message: String): Unit = {
    if (terminal == null) {
      // fallback
      println(message)
    } else {
      terminal.writer().println(message)
      terminal.flush() // 不是必须，但聊胜于无
    }

  }

  def print(message: String): Unit = {
    if (terminal == null) {
      print(message)
    } else {
      terminal.writer().print(message)
      terminal.flush()
    }
  }

}