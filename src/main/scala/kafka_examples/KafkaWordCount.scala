import java.util
import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import scala.collection.immutable.Map




object KafkaWordCount {



  def main(args:Array[String]) = {




/*    if(args.length < 3) {
      System.err.println("Usage:KafkaWordCount <zkQuorum><consumerGroup><topic>")
      System.exit(1)
    }*/


    val topics = Array("nanda_test")


    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "consumerGroup",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )


    val conf = new SparkConf().setMaster("local[*]").setAppName("KafkaWordCount")
    val ssc = new StreamingContext(conf, Seconds(2))
    ssc.sparkContext.setLogLevel("ERROR")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    var lines =stream.map(record => record.value)

    val words =lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word,1))
    val wordCount = pairs.reduceByKey(_+_)
    wordCount.print()
    ssc.start()
    ssc.awaitTermination()

  }

}

