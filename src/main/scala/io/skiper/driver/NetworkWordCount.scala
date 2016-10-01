package io.skiper.driver

/**
  * Created by P088466 on 2016-07-06.
  */

import io.skiper.writer.{ESWriter, RedisConnector}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.scheduler.{StreamingListenerBatchStarted, _}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.spark.rdd.EsSpark

import scala.collection.mutable
//import scala.collection.mutable.{HashMap, LinkedHashMap}



/**
  * Counts words in UTF8 encoded, '\n' delimited text received from the network every second.
  *
  * Usage: NetworkWordCount <hostname> <port>
  * <hostname> and <port> describe the TCP server that Spark Streaming would connect to receive data.
  *
  * To run this on your local machine, you need to first run a Netcat server
  * `$ nc -lk 9999`
  * and then run the example
  * `$ bin/run-example org.apache.spark.examples.streaming.NetworkWordCount localhost 9999`
  */
object NetworkWordCount {
  def main(args: Array[String]) {

    var argsv = new Array[String](2)
    if (args.length < 2) {
      System.err.println("Usage: NetworkWordCount <hostname> <port>")
      argsv = Array("1.234.69.44", "9999")
    }
    else {
      argsv = Array(args(0), args(1))
    }
    argsv = Array("1.234.69.44", "9999")

    // Connection setting
    val redis = new RedisConnector(argsv(0))

    // Create the context with a 1 second batch size
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("Simple-Streaming")
    sparkConf.set("es.index.auto.create", "true");
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    addStreamListener(ssc)

    // Create Kafka Receiver
//    val lines = ssc.socketTextStream(argsv(0), argsv(1).toInt, StorageLevel.MEMORY_AND_DISK_SER)
    val Array(zkQuorum, group, topics, numThreads) = Array("1.234.69.44" ,"my-consumer-group", "realtime", "1")
    ssc.checkpoint("checkpoint")
    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
    println(lines)

    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)

    // parse
    val columnList = List("cnt", "srcip", "dstip")
    val wordList = lines.mapPartitions(iter => {
      iter.toList.map(s => {
        val listMap = new mutable.LinkedHashMap[String, Any]()
        val split = s.split(" ")
        listMap.put(columnList(0), split(0).toInt)
        listMap.put(columnList(1), split(1))
        listMap.put(columnList(2), split(2))
        println(s" map = ${listMap.toString()}")
        listMap
      }).iterator
    })

    // Write to ElasticSearch
    wordList.foreachRDD(rdd => {
      rdd.foreach(s => s.foreach(x => println(x.toString)))
      //EsSpark.saveToEs(rdd, "realtime/blacklist")
    })

    wordCounts.print()
    println("=====================End Sptreaming")
    ssc.start()
    ssc.awaitTermination()
  }



  def addStreamListener(ssc: StreamingContext): Unit = {
    ssc.addStreamingListener(new StreamingListener() {
      override def onBatchSubmitted(batchSubmitted: StreamingListenerBatchSubmitted): Unit = {
        super.onBatchSubmitted(batchSubmitted)
        val batchTime = batchSubmitted.batchInfo.batchTime
        //println("[batchSubmitted] " + batchTime.milliseconds)
      }

      override def onBatchStarted(batchStarted: StreamingListenerBatchStarted): Unit = {
        super.onBatchStarted(batchStarted)
        val batchStartTime = batchStarted.batchInfo.batchTime.milliseconds
        //println("[onBatchStarted] " + batchStartTime)

        //check Black List IP
        if (true) {
          //redis.putLog("mykey", batchStartTime.toString)
        }
      }
    })
  }

  def detectBlackList(ip: String): Boolean = {

    true
  }

  def testDstream(ssc: StreamingContext): Unit = {
    var lines = mutable.LinkedHashMap[String, String]()
    val lines2 = mutable.Queue[RDD[String]]()
    val dstream = ssc.queueStream(lines2)

    lines += ("K1" -> "V2")



  }
}