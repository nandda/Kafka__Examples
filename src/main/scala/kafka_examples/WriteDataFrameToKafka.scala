package kafka_examples

import org.apache.spark.sql.SparkSession

object WriteDataFrameToKafka  {

  def main(args:Array[String]): Unit = {

    val spark = SparkSession.builder().appName("Kafka_examples_dataFrame").master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext
    sc.setLogLevel("ERROR")

    val data = Seq(("Moto","2007"),("iphone 35","2008"),
      ("iphone 3GS","2009"),
      ("iphone 4","2010"),
      ("iphone 4S","2011"),
      ("iphone 5","2012"),
      ("iphone 8","2014"),
      ("iphone 10","2017"))

    val df = spark.createDataFrame(data).toDF("key","value")

    df.write
      .format("kafka")
      .option("kafka.bootstrap.servers","localhost:9092")
      .option("enable.auto.commit","true")
      .option("auto.commit.interval.ms","1000")
      .option("topic","nanda_test")
      .save()

  }
}
