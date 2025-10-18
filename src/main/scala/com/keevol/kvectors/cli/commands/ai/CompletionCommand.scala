package com.keevol.kvectors.cli.commands.ai

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

import com.keevol.kvectors.cli.TerminalAttached
import com.keevol.kvectors.cli.utils.Ollama
import org.apache.commons.lang3.StringUtils
import picocli.CommandLine.{Command, Parameters}


@Command(name = "complete",
  aliases = Array("ask", "chat"),
  mixinStandardHelpOptions = true,
  description = Array("ask LLM model to complete the message"))
class CompletionCommand extends Runnable with TerminalAttached {

  @Parameters(index = "0", arity = "1", description = Array("the message that will be sent to LLM"))
  var message: String = _

  override def run(): Unit = {
    if (StringUtils.isEmpty(message)) {
      echo("you must provide a message to complete with model")
      return
    }
    echo(Ollama.complete(message))
  }
}