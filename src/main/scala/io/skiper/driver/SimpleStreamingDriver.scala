package io.skiper.driver

import io.skiper.writer.RedisConnector
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.scheduler.{StreamingListener, StreamingListenerBatchCompleted, StreamingListenerBatchStarted, StreamingListenerBatchSubmitted}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.spark.rdd.EsSpark

import scala.collection.mutable

/**
  * Created by P088466 on 2016-07-12.
  */
object SimpleStreamingDriver {
  def main(args: Array[String]) {

    var argsv = new Array[String](2)
    if (args.length >= 2) {
      argsv = Array(args(0), args(1))
    }
    else {
      System.err.println("Usage: NetworkWordCount <hostname> <port>")
      argsv = Array("dataservice-324", "9999")
    }

    // Connection setting
    val redis = new RedisConnector(argsv(0))


    // Create the context with a 1 second batch size
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("Simple-Streaming")
    /*sparkConf.set("es.index.auto.create", "true");
    sparkConf.set("es.nodes", "dataservice-324")*/
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    addStreamListener(ssc, redis)

    // Create Kafka Receiver//
    // 10 1.1.1.1  2.2.2.2  //lines
    // 20  1.1.1.1  3.3.3.3
    // 40 1.1.1. 4.4.4.4

    val Array(zkQuorum, group, topics, numThreads) = Array("localhost:2181" ,"my-consumer-group", "realtime", "1")
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
/*    wordList.foreachRDD(rdd => {
      rdd.foreach(s => s.foreach(x => println(x.toString)))
      EsSpark.saveToEs(rdd, "realtime/blacklist")
    })*/

    // Write to Redis


    wordCounts.print()
    ssc.start()
    ssc.awaitTermination()
  }



  def addStreamListener(ssc: StreamingContext, redis: RedisConnector): Unit = {
    ssc.addStreamingListener(new StreamingListener() {
      override def onBatchSubmitted(batchSubmitted: StreamingListenerBatchSubmitted): Unit = {
        super.onBatchSubmitted(batchSubmitted)
        val batchTime = batchSubmitted.batchInfo.batchTime
        //println("[batchSubmitted] " + batchTime.milliseconds)
      }

      override def onBatchStarted(batchStarted: StreamingListenerBatchStarted): Unit = {
        super.onBatchStarted(batchStarted)

      }

      override def onBatchCompleted(batchCompleted: StreamingListenerBatchCompleted): Unit = {
        super.onBatchCompleted(batchCompleted)

        var total_delay = batchCompleted.batchInfo.totalDelay
        var process_delay = batchCompleted.batchInfo.processingDelay

        //check Black List IP
        if (true) {
          //redis
          //redis.putLog("mykey", batchStartTime.toString)
        }


//        accumulableExecuteLogs.value.map(s => {
//          //println("accumulated execution map : " + s._1 + " / " + s._2)
//          val key = flow.id + ":" + s._1 + ":" + formatter.format(batchStartTime) + ":" + formatter.format(batchEndTime)
//          val lastExe = accumulableLastExecuted.value
//          redisLogger.putLog(key, lastExe(s._1) + "," + s._2)
//        })
//        accumulableExecuteLogs.value.clear()
      }

    })
  }

  def detectBlackList(ip: String): Boolean = {

    true
  }
}
