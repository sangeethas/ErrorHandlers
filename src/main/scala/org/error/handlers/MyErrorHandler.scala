package org.error.handlers

import org.elasticsearch.hadoop.handler.HandlerResult
import org.elasticsearch.hadoop.rest.bulk.handler.{BulkWriteErrorHandler, BulkWriteFailure, DelayableErrorCollector}
import org.slf4j.LoggerFactory

class MyErrorHandler extends BulkWriteErrorHandler {

  val logger = LoggerFactory.getLogger(this.getClass)

  @throws[Exception]
  def onError(entry: BulkWriteFailure, collector: DelayableErrorCollector[Array[Byte]]): HandlerResult = {
    logger.info("Entered error handler")
    println("Entered error handler")
    if (entry.getResponseCode == 400) {
      logger.warn("Encountered conflict response. Ignoring old data.")
      println("error handled")
      return HandlerResult.HANDLED
    }
    logger.info("Exit error handler")
    println("error passed")
    collector.pass("Not a conflict response code.")
  }
}
