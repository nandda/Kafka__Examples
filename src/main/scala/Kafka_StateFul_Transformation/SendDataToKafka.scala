package Kafka_StateFul_Transformation

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object SendDataToKafka {

  def main(args:Array[String]): Unit = {

    val inputTopic = "demo"
    val broker = "localhost:9092"

    val properties = new Properties()
    properties.put("bootstrap.servers",broker)
    properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer")
    properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String,String](properties)
    val message ="I love India"
    var key =0

    while (key < 5) {
      key = key + 1
      val record = new ProducerRecord[String,String](inputTopic,key.toString,message)
      producer.send(record).get().toString
      println(s"inserted data :$key")
    }

    producer.close()

  }

}
