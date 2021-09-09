package org.error.handlers

import org.elasticsearch.hadoop.serialization.handler.write.SerializationErrorHandler
import org.slf4j.LoggerFactory

class MySerializationErrorHandler extends SerializationErrorHandler {

  val logger = LoggerFactory.getLogger("SerializationErrorHandler")

  import org.elasticsearch.hadoop.handler.{ErrorCollector, HandlerResult}
  import org.elasticsearch.hadoop.serialization.handler.write.SerializationFailure

  @throws[Exception]
  def onError(entry: SerializationFailure, collector: ErrorCollector[AnyRef]): HandlerResult = {
    val record = entry.getRecord.asInstanceOf[Nothing]
    logger.error("Could not serialize record. ")
    HandlerResult.HANDLED
  }
}
