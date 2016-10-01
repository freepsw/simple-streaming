package io.skiper.writer

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.EsSpark

/**
  * Created by P088466 on 2016-07-11.
  */
case class ESWriter(indexs: String, types: String)

object ESWriter {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("test").setMaster("local[2]")
    conf.set("es.index.auto.create", "true")
    val sc = new SparkContext(conf)

    testMapPartitions3 (sc)
  }

  def testES(sc: SparkContext): Unit = {
    val numbers = Map("one" -> 4, "two" -> 8, "three" -> 12)
    val airports = Map("arrival" -> "Otopeni", "SFO" -> "San Fran")

    val rdd = Seq(numbers, airports)
    sc.makeRDD(rdd).saveToEs("spark/docs")

    // EsSpark.saveToEs(rdd, "spark/docs")
  }

  def testMapPartitions(sc:SparkContext):Unit = {
    val a = sc.parallelize(1 to 9, 1)
    def myfunc[T](iter: Iterator[T]) : Iterator[(T, T)] = {
      var res = List[(T, T)]()
      var pre = iter.next
      while (iter.hasNext)
      {
        val cur = iter.next;
        res .::= (pre, cur)
        pre = cur;
      }
      res.iterator
    }
    a.mapPartitions(myfunc).collect.foreach(println)
  }

  def testMapPartitions2(sc:SparkContext):Unit = {
    val x = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8, 9,10), 1)
    def myfunc(iter: Iterator[Int]) : Iterator[Int] = {
      var res = List[Int]()
      while (iter.hasNext) {
        val cur = iter.next;
        val random = scala.util.Random.nextInt(10)
        res = res ::: List.fill(random)(cur)
        println(s"r : ${random} vs cnt= $cur")
        println(s"List = $res")
      }
      res.iterator
    }
    x.mapPartitions(myfunc).collect
  }

  def testMapPartitions3(sc:SparkContext):Unit = {
    val column = List("cnt", "dstip", "srcip")
    val x = sc.parallelize(List("3", "dstIP","srcIP"), 1)
    var y = List[String]()

    // 다른 task의 partition들을 driver로 collect한 후
    // rdd에 대한 연산 진행
    x.collect.foreach(rdd => {
      y = y :+ rdd
    })
    println(s"y = $y")

    // 위와 다른 점은 개별 partitino에서 list를 생성한다.
    // 따라서 list 객체를 partition내부에 생성
    x.foreachPartition(rdd => {
      var list = List[String]()
      rdd.foreach(s => {
        list = list :+ s
      })
      println(s"list = $list")
    })

    val z = y.zip(column)

    println(z)

    def myfunc(iter: Iterator[Int]) : Iterator[Int] = {
      var res = List[Int]()
      while (iter.hasNext) {
        val cur = iter.next;
        val random = scala.util.Random.nextInt(10)
        res = res ::: List.fill(random)(cur)
        println(s"r : ${random} vs cnt= $cur")
        println(s"List = $res")
      }
      res.iterator
    }
    //x.mapPartitions(myfunc).collect
  }


  /*
    def testES(rdd: record): Unit = {
      val numbers = Map("one" -> 4, "two" -> 8, "three" -> 12)
      val airports = Map("arrival" -> "Otopeni", "SFO" -> "San Fran")

      //EsSpark.
      // EsSpark.saveToEs(rdd, "spark/docs")
    }*/
}

