package com.keevol.kvectors.cli.utils

import com.keevol.kvectors.utils.Http
import io.vertx.core.json.{JsonArray, JsonObject}

import java.net.URI

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
object Ollama {
  /**
   * complete with ollama model.
   *
   * @param message the prompt message
   * @return
   */
  def complete(message: String, model: String = "qwen3:0.6b"): String = {
    //  run `ollama serve` to start ollama first, then run api requests below.
    // https://ollama.readthedocs.io/en/api/#generate-a-completion
    val payload = JsonObject.of("model", model, "prompt", message, "stream", false) // https://medium.com/@kevinnjagi83/exploring-ollama-rest-api-endpoints-7029fae5630d
    //    val r = Unirest.post("http://localhost:11434/api/generate").contentType("application/json").body(payload.encode()).asString()
    Http.postJson(URI.create("http://localhost:11434/api/generate").toURL, payload)
  }

  /**
   * embedding response sample:
   * <pre>
   * {
   *    "model": "all-minilm",
   *    "embeddings": [[
   *      0.010071029, -0.0017594862, 0.05007221, 0.04692972, 0.054916814,
   *      0.008599704, 0.105441414, -0.025878139, 0.12958129, 0.031952348
   *    ]],
   *    "total_duration": 14143917,
   *    "load_duration": 1019500,
   *    "prompt_eval_count": 8
   * }
   * </pre>
   */
  def embedding(message: String, embeddingModel: String = "dengcao/Qwen3-Embedding-0.6B:Q8_0"): String = {
    val embeddingRequest = JsonObject.of("model", embeddingModel, "input", message)
    val responseBody = Http.postJson(URI.create("http://localhost:11434/api/embed").toURL, embeddingRequest)
    //    new JsonObject(responseBody).getJsonArray("embeddings").getJsonArray(0)
    responseBody
  }

  def main(args: Array[String]): Unit = {

    println(embedding("你从哪儿来？"))
    println(embedding("你是哪里人？"))
    // similarity score: 0.796120
  }
}