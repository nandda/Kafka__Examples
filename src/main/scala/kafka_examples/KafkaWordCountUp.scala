package kafka_examples

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import  org.apache.spark.streaming.kafka010._
import  org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
object KafkaWordCountUp extends App{
  val topics = Array("nanda_test")

  def updateFunction(newValues:Seq[Int],runningCount:Option[Int]) = {
    var result:Option[Int] =null

    if(newValues.isEmpty) {
      result=Some(runningCount.get)
    }
    else {
      result =Some(newValues.reduce(_+_))
      if(!runningCount.isEmpty)
        result = Some(result.get + runningCount.get)
    }
    result
  }

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
  ssc.checkpoint("mycheckpointdir")
  ssc.sparkContext.setLogLevel("ERROR")
  val stream = KafkaUtils.createDirectStream[String, String](
    ssc,
    PreferConsistent,
    Subscribe[String, String]((topics), kafkaParams)
  )

  var lines = stream.map(record => record.value())

  val words = lines.flatMap(_.split(" "))

  val pairs = words.map(word => (word,1))

  val wordCounts = pairs.reduceByKey(_+_)

  var updateRDD = wordCounts.updateStateByKey(updateFunction)

  updateRDD.print()

  ssc.start()
  ssc.awaitTermination()

}
