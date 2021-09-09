package org.home.spark

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

class MyKafkaProducer (kafkaKey: String, kafkaValue: String) {

  val kafkaProducerProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "x.data.edh:6667")
    props.put("key.serializer", classOf[StringSerializer].getName)
    props.put("value.serializer", classOf[StringSerializer].getName)
    props
  }

  def kafkaSend() = {
    val producer = new KafkaProducer[String, String](kafkaProducerProps)
    producer.send(new ProducerRecord[String, String]("errorHandlerTopic", kafkaKey, kafkaValue))
  }

}
