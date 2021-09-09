package org.home.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, struct}
import za.co.absa.abris.avro.parsing.utils.AvroSchemaUtils
import za.co.absa.abris.examples.data.generation.TestSchemas

object SparkSchemaStreamWrite extends App {

  val spark = SparkSession
    .builder
    .appName("Spark-Kafka-Integration")
    .master("local")
    .getOrCreate()

  val kafkaInputTopic = "schema-input"
  val kafkaOutputTopic = "schema-output"

  import za.co.absa.abris.config.AbrisConfig


  val df = spark
    .read
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", kafkaInputTopic)
    .load()


  // to serialize all columns in dataFrame we need to put them in a spark struct
  val allColumns = struct( df.columns.head, df.columns.tail: _*)
  val expectedSchemaString = TestSchemas.COMPLEX_SCHEMA_SPEC
  val schemaString = """{
        "fields": [
            { "name":  "id",      "type": "string"},
            { "name":  "name",    "type": "string"}
        ],
        "name": "Person",
        "type": "record"
    }"""

//  AvroSchemaUtils.parse(schemaString)


  val abrisConfig = AbrisConfig
    .toConfluentAvro
    .provideAndRegisterSchema(schemaString)
    .usingTopicNameStrategy(kafkaInputTopic)
    .usingSchemaRegistry("http://localhost:8081")

  import za.co.absa.abris.avro.functions.to_avro

  val avroFrame = df.select(to_avro(allColumns, abrisConfig) as 'value)

  avroFrame
    .write
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("topic", kafkaOutputTopic)
    .save()

}
