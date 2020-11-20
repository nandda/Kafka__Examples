package Kafka_StateFul_Transformation

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.KafkaUtils
import  org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import  org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.KafkaUtils
object WordOccurrenceUpdateStateByKey {

  def main(args:Array[String]): Unit = {

    val spark = SparkSession.builder().master("local[2]").appName("implementation_updateState").getOrCreate()
    val sc = spark.sparkContext
    sc.setLogLevel("ERROR")
    val ssc = new StreamingContext(sc,Seconds(5))

    val interval = 15
    val kafkaParams = Map[String,Object] (
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "spark_updateStateByKey",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false:java.lang.Boolean)
    )
    val topics = Seq("demo")

    val kafkaStream = KafkaUtils.createDirectStream[String,String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val splits =kafkaStream.map(record=> (record.key(),record.value())).flatMap(x=> x._2.split(" "))

    val updateFunc = (value:Seq[Int],state:Option[Int]) => {
      val currentCount = value.sum
      val previousCount = state.getOrElse(0)
      Some(currentCount + previousCount)
    }

    ssc.checkpoint("D:\\Development\\Kafka_examples\\src\\main\\scala\\Kafka_StateFul_Transformation\\CheckPoint")
    val wordCounts = splits.map(x=> (x,1)).reduceByKey(_+_)
      .updateStateByKey(updateFunc).checkpoint(Seconds(interval.toLong))

    wordCounts.print()
    ssc.start()
    ssc.awaitTermination()

  }



}
