package io.skiper.driver

import org.apache.spark.SparkConf
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.hive.thriftserver._
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils

import scala.collection.mutable
import scala.collection.mutable.{LinkedHashMap}
import io.skiper.sql.{HiveContextSingleton, RddSchema}
/**
  * Created by P088466 on 2016-08-08.
  */
object StreamingSql {
  def main(args: Array[String]) {

    println("Start Streaming SQL process")

    // Create the context with a 1 second batch size
    val sparkConf = new SparkConf()//.setAppName("Simple-Streaming").setMaster("local[2]")
    sparkConf.set("spark.driver.allowMultipleContexts", "true")

    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Create Kafka Receiver
    //val Array(zkQuorum, group, topics, numThreads) = Array("dataservice-324:2181", "my-consumer-group", "realtime", "1")
    val Array(zkQuorum, group, topics, numThreads) = Array("1.234.69.44:3181", "my-consumer-group", "realtime", "1")
    ssc.checkpoint("checkpoint")
    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
    println(lines)

    val words = lines.flatMap(_.split(" "))
    //val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)

    // parse
    val columnTypeList = List("int", "string", "string")
    val columnList = List("cnt", "srcip", "dstip")

    val wordList = lines.mapPartitions(iter => {
      iter.toList.map(s => {
        val listMap = new mutable.LinkedHashMap[String, String]()
        val split = s.split(",")
        listMap.put(columnList(0), split(0))
        listMap.put(columnList(1), split(1))
        listMap.put(columnList(2), split(2))
        println(s" map = ${listMap.toString()}")
        listMap
      }).iterator
    })

    //01. create schema (column name : column type)
    val schema = new RddSchema()
    schema.createSchema(columnList.toArray, columnTypeList.toArray)

    //wordCounts.print()
    wordList.foreachRDD( rdd => {
      rdd.foreach(x => println(s"xx => ${x.toString}"))
    })

    //02. RDD to  DataFrame (spark 2.0에서는 Dataframe 기반)
    wordList.window(Seconds(30)).foreachRDD { record =>
      println(s"record Count = ${record.count()}")
      val sqlContext = HiveContextSingleton.getInstance(record.sparkContext, "20000")
      val rowRDD = schema.parseRow(record)
      val df = sqlContext.createDataFrame(rowRDD, schema.getSchema())
      df.registerTempTable("default")
      println("registerTable")

      //df.take(1).foreach(row => println(row.toString()) )
//      df.printSchema()
//      df.

      sqlContext.sql("SELECT sum(cnt) FROM default").collect().foreach(println)
    }



    ssc.start()
    ssc.awaitTermination()
  }
}
