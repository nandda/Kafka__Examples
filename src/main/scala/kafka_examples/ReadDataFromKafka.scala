package kafka_examples

import org.apache.avro.io.Encoder
import org.apache.spark.sql.SparkSession

object ReadDataFromKafka {

  def main(args:Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Read_data").master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext
    sc.setLogLevel("ERROR")

    val df = spark
      .read
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "nanda_test")
      .option("endingOffsets", "latest")
      .load()

    df.printSchema()

    val df2 = df.selectExpr("cast(key as STRING)","cast(value as STRING)")
    df2.show(100,false)
  }

}
