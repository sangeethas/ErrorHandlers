package org.home.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col

object SparkSchemaStreamRead extends App {

  val spark = SparkSession
    .builder
    .appName("Spark-Kafka-Integration")
    .master("local")
    .getOrCreate()

  val kafkaTopicName = "schema-input"

  import za.co.absa.abris.config.AbrisConfig


  val df = spark
    .read
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", kafkaTopicName)
    .load()

  val abrisConfig = AbrisConfig
    .fromConfluentAvro
    .downloadReaderSchemaByLatestVersion
    .andTopicNameStrategy(kafkaTopicName)
    .usingSchemaRegistry("http://localhost:8081")

  import za.co.absa.abris.avro.functions.from_avro

  val deserialized = df.select(from_avro(col("value"), abrisConfig) as 'data)

  deserialized.printSchema()

  deserialized
    .writeStream
    .format("console")
    .option("truncate", "false")
    .start()
    .awaitTermination()

}
