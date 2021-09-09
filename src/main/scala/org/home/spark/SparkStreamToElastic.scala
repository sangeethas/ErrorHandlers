package org.home.spark

import org.apache.spark.sql.SparkSession
import org.elasticsearch.hadoop.cfg.ConfigurationOptions

object SparkStreamToElastic extends App {

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
    .config("es.write.rest.error.handlers", "sparkErrorHandler")
    .config("es.write.rest.error.handler.sparkErrorHandler", "org.error.handlers.MyErrorHandler")
    .master("local[*]")
    .appName("sample-structured-streaming")
    .getOrCreate()

  val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "quickstart-elastic-events")
    .option("auto.offset.reset", "earliest")
    .load()

//  df.printSchema()

//  IndexUtils.readonly()
//  IndexUtils.forcemerge()

println("writing data to elastic")
  df.writeStream
    .outputMode("append")
    .format("org.elasticsearch.spark.sql")
    .option("checkpointLocation", "../SparkStream/src/main/resources")
    .option(ConfigurationOptions.ES_NET_HTTP_AUTH_USER, "elastic")
    .option(ConfigurationOptions.ES_NET_HTTP_AUTH_PASS, "elastic")
    .option(ConfigurationOptions.ES_NODES, "localhost")
    .option(ConfigurationOptions.ES_PORT, "9200")
    .start("pds-payments1/doc-type").awaitTermination()


}
