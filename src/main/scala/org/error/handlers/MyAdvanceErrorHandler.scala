package org.error.handlers

import org.apache.commons.io.IOUtils
import org.elasticsearch.hadoop.handler.HandlerResult
import org.elasticsearch.hadoop.rest.bulk.handler.{BulkWriteErrorHandler, BulkWriteFailure, DelayableErrorCollector}

import java.io._
import java.util.Properties

class MyAdvanceErrorHandler extends BulkWriteErrorHandler {

  private var writer: BufferedWriter = null
  private var outputStream: BufferedWriter = null

  override def init(properties: Properties): Unit = {
    println("Entered Advanced Error handler")
    try {
//      val f = new File("../IdeaProjects/SparkStream/src/main/resources/output.json")
      val outputStream = new FileOutputStream(properties.getProperty("filename"))   // ../SparkStream/src/main/resources/output.json
      writer = new BufferedWriter(new OutputStreamWriter(outputStream))
    } catch {
      case e: FileNotFoundException =>
        throw new RuntimeException("Could not open file", e)
    }
  }

  override def onError(entry: BulkWriteFailure, collector: DelayableErrorCollector[Array[Byte]]): HandlerResult = {
    writer.write("Code: " + entry.getResponseCode)
    println("Entered onError: " + entry.getResponseCode)
    writer.newLine
    writer.write("Error: " + entry.getException.getMessage)
    writer.newLine
    import scala.collection.JavaConversions._
    for (message <- entry.previousHandlerMessages) {
      writer.write("Previous Handler: " + message)
      writer.newLine
    }
    writer.write("Attempts: " + entry.getNumberOfAttempts)
    writer.newLine
    writer.write("Entry: ")
    writer.newLine
    println("entry.getEntryContents: " + entry.getEntryContents)
    IOUtils.copy(entry.getEntryContents, writer)


    writer.newLine
    println("End of onError: " + writer.toString)
    HandlerResult.HANDLED
//    println("Writing to Kafka")
//    new MyKafkaProducer(entry.getEntryContents)


  }

  override def close(): Unit = {
    try {
      writer.close
//      outputStream.close
    } catch {
      case e: IOException =>
        throw new RuntimeException("Closing file failed", e)
    }
  }


}
