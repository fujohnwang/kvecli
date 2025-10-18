package com.keevol.kvectors.cli.commands

import com.keevol.kvectors.KVectors
import com.keevol.kvectors.cli.TerminalAttached
import io.vertx.core.json.{JsonArray, JsonObject}
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import picocli.CommandLine.{Command, Parameters}

import java.util.concurrent.atomic.AtomicReference
import java.util.stream.Collectors
import scala.collection.JavaConverters._

@Command(name = "query",
  aliases = Array("so", "search"),
  mixinStandardHelpOptions = true,
  description = Array("search similar vectors as per provided one."))
class SimilaritySearchCommand(kdb: KVectors) extends Runnable with TerminalAttached {
  @Parameters(index = "0", arity = "1", description = Array("collection name"))
  var collectionName: String = _

  @Parameters(index = "1", arity = "1", description = Array("query vector data"))
  var vectorInJsonArrayString: String = _

  override def run(): Unit = {
    if (StringUtils.isAnyEmpty(collectionName, vectorInJsonArrayString)) {
      echo("you must provide all of the 'collection name', 'vector data' parameters.")
      return
    }

    val parsedException = new AtomicReference[Throwable]
    try {
      new JsonArray(vectorInJsonArrayString)
    } catch {
      case t: Throwable => parsedException.set(t)
    }
    if (parsedException.get() != null) {
      echo(s"invalid parameter : ${ExceptionUtils.getStackTrace(parsedException.get())}")
      return
    }

    val collectionOption = kdb.getCollection(collectionName)
    if (collectionOption.isEmpty) {
      echo(s"there is no such collection: ${collectionName} to add vector to.")
      return
    }
    val vectorCollection = collectionOption.get

    val vector = new JsonArray(vectorInJsonArrayString).getList // 1. 获取底层的 java.util.List<Object>
      .asScala // 2. 将其转换为 Scala 的 Buffer[Object]
      .map { // 3. 遍历每个元素并进行安全的转换
        case n: java.lang.Number => n.floatValue() // 4. 如果是数字，安全地转为 Float
        case other => throw new ClassCastException(s"无法将'${other}'转换为Float")
      }
      .toArray // 5. 将最终的集合转换为 Array[Float]

    val resultList = vectorCollection.query(vector)
    echo(s"found ${resultList.size()} similar vectors, listed below: ")
    resultList.forEach(v => {
      echo(v.toString)
    })
  }
}