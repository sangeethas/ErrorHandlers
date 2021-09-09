package org.home.spark

import org.apache.spark.sql.SparkSession
import org.elasticsearch.hadoop.cfg.ConfigurationOptions
import org.error.handlers.IndexUtils

object ReadJsonToElastic extends App {

  val spark = SparkSession
    .builder
    .appName("Spark-Kafka-Integration")
    .master("local")
    .getOrCreate()

  val sparkSession = SparkSession.builder()
    .config(ConfigurationOptions.ES_NET_HTTP_AUTH_USER, "elastic")
    .config(ConfigurationOptions.ES_NET_HTTP_AUTH_PASS, "elastic")
    .config(ConfigurationOptions.ES_NODES, "localhost")
    .config(ConfigurationOptions.ES_PORT, "9200")
//    .config("es.write.rest.error.handlers", "sparkErrorHandler")
//    .config("es.write.rest.error.handler.sparkErrorHandler", "org.error.handlers.MyErrorHandler")
    .config("es.index.auto.create", "true")
    .master("local[*]")
    .appName("sample-structured-streaming")
    .getOrCreate()


  val df = spark.read.json("../SparkStream/src/main/resources/sample.json")

//  df.printSchema()

  IndexUtils.readonly()
//  IndexUtils.forcemerge()

  df.write.format("console").save()

println("writing data to elastic")
  df.write
    .format("org.elasticsearch.spark.sql")
    .mode("append")
    .option("checkpointLocation", "\\Users\\sravanisangeetha\\IdeaProjects\\SparkStream\\src\\main\\resources")
    .option("es.resource", "pds-payments1/doc-type")
    .option("es.index.auto.create", "true")
    .option(ConfigurationOptions.ES_NET_HTTP_AUTH_USER, "elastic")
    .option(ConfigurationOptions.ES_NET_HTTP_AUTH_PASS, "elastic")
    .option("es.write.rest.error.handlers", "myerror, myadvanceerror")
    .option("es.write.rest.error.handler.myerror", "org.error.handlers.MyErrorHandler")
    .option("es.write.rest.error.handler.myadvanceerror", "org.error.handlers.MyAdvanceErrorHandler")
    .option("es.write.rest.error.handler.myadvanceerror.filename", "../SparkStream/src/main/resources/output.json")
    .option(ConfigurationOptions.ES_NODES, "localhost")
    .option(ConfigurationOptions.ES_PORT, "9200")
    .save()

}
