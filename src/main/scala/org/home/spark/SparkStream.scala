package org.home.spark

import org.apache.spark.sql.SparkSession

object SparkStream extends App {

  val spark = SparkSession
    .builder
    .appName("Spark-Kafka-Integration")
    .master("local")
    .getOrCreate()

  val df = spark
    .read
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "quickstart-input-events")
    .load()

  import spark.implicits._

  df.printSchema()

  df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
    .as[(String, String)]
    .write
    .format("kafka")
    .option("topic", "quickstart-output-events")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("checkpointLocation", "\\Users\\sravanisangeetha\\IdeaProjects\\SparkStream\\src\\main\\resources")
    .save()

//  df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
//    .as[(String, String)]
//    .write
//    .format("console")
//    .option("truncate","false")
//    .save()


}
