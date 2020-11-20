package Kafka_StateFul_Transformation

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
object WorkOccurrenceMapWithState {

  def main(args:Array[String]): Unit = {

    val spark = SparkSession.builder().appName("MapwithState").master("local[*]")
      .getOrCreate()
    val sc = spark.sparkContext
    sc.setLogLevel("ERROR")

    val ssc = new StreamingContext(sc,Seconds(5))
    ssc.checkpoint("D:\\Development\\Kafka_examples\\src\\main\\scala\\Kafka_StateFul_Transformation\\CheckPoint")

    val KafkaParam = Map[String,Object] (
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "spark_mapWithState",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false:java.lang.Boolean)

    )

    val topics = Seq("demo")
    val kafkaStream = KafkaUtils.createDirectStream[String,String](
      ssc,
      PreferConsistent,
      Subscribe[String,String](topics,KafkaParam)
    )

    val splits = kafkaStream.map(record => (record.key(),record.value() )).flatMap(x=> x._2.split(" "))

    val mappingFunc = (word:String,one:Option[Int],state:State[Int]) => {
      val sum = one.getOrElse(0) + state.getOption().getOrElse(0)
      state.update(sum)
      (word,sum)
    }

    val wordCount =splits.map(x=> (x,1)).reduceByKey(_+_)
      .mapWithState(StateSpec.function(mappingFunc)).checkpoint(Seconds(15))
    wordCount.print()
    ssc.start()
    ssc.awaitTermination()

  }

}
