package kafka_examples

import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import scala.collection.JavaConverters._

/*Kafka consumer read data from Topic
once you run the kafka consumer then you run kafkaProducer */


object KafkaConsumerSubscribeApp extends App {

  val props = new Properties()
  props.put("group.id","test")
  props.put("bootstrap.servers","localhost:9092")
  props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer")
  props.put("enable.auto.commit","true")
  props.put("auto.commit.interval.ms","1000")

  val consumer = new KafkaConsumer(props)
  val topics = List("nanda_test")

  try {
    consumer.subscribe(topics.asJava)
    while(true) {
      val records = consumer.poll(10)
      for (record <- records.asScala) {
        println("Topic: "+record.topic() +
            ",Key: " + record.key() +
             ",Value : "+ record.value() +
             ", offset :"+ record.offset() +
            ", Partition: "+record.partition())
      }
    }

  }catch {
    case e:Exception => e.printStackTrace()
  }finally  {
    consumer.close()
  }


}
