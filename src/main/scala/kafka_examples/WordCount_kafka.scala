/*
package kafka_examples

import org.apache.spark._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.kafka.KafkaUtils
//import org.apache.spark.streaming.kafka010.KafkaUtils

//https://acadgild.com/blog/spark-streaming-kafka-integration

//Need to change the pom file //
/*<!-- <dependency>
      <groupId>org.apache.spark</groupId>
      <artifactId>spark-streaming-kafka-0-8_2.11</artifactId>
      <version>2.4.1</version>
    </dependency>-->
*/
object WordCount_kafka {

  def main(args:Array[String]): Unit = {

  //  val spark = SparkSession.builder().appName("Word_count_kafka").master("local[*]").getOrCreate()

    val conf = new SparkConf().setMaster("local[*]").setAppName("KafkaReceiver")
    val ssc = new StreamingContext(conf,Seconds(5))

   // val kafkaStream = KafkaUtils.createDirectStream(ssc,"localhost:2181","spark-streaming-consumer-group",Map("nanda_test" ->5))

    val kafkaStream =KafkaUtils.createStream(ssc,"localhost:2181","spark-streaming-consumer-group",Map("nanda_test" ->5))

   val words = kafkaStream.flatMap(x=> x._2.split(" "))

    //val words = kafkaStream.flatMap(x=> x._1.split(" "))

    val wordCounts =words.map(x=> (x,1)).reduceByKey(_+_)
    kafkaStream.print()
    wordCounts.print()
    ssc.start()
    ssc.awaitTermination()
  }

}
*/
