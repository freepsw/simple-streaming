package io.skiper.sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._

import scala.collection.mutable.LinkedHashMap

/**
 * DateFrame 생성을 위한 스키마를 정의하는 Class
 */
class RddSchema extends Serializable {
  private var schema: StructType = _
  private var types: Array[String] = _

  /**
   * 지정된 컬럼 이름과 타입으로 스키마를 생성.
   *
   * Web에서 string, number로 불러온 타입 이름에 대해 각각 StringType, LongType으로 정의함.
   *
   * Thrift Server에서는 string, bigint 타입으로 사용함.
   */
  def createSchema(c: Array[String], t: Array[String]) {
    val column = new Array[StructField](c.length)
    types = t

    for (i <- 0 to (c.length - 1)) {
      if (t(i) equals "string") {
        column(i) = StructField(c(i), StringType, true)
      } else if (t(i) equals "number") {
        column(i) = StructField(c(i), LongType, true)
      } else if (t(i) equals "int") {
        column(i) = StructField(c(i), IntegerType, true)
      } else {
        column(i) = StructField(c(i), StringType, true)
      }
    }

    schema = StructType(column)
  }

  /**
   * 이름과 타입으로 정의된 필드를 스키마에 추가.
   *
   * 이전에 정해진 스키마에 새로운 필드를 Append하는 방식으로 추가.
   */
  def addFields(nc: Array[String], t: Array[String]) {
    var schemaArray = schema.toArray

    for ((key, i) <- nc.zipWithIndex) {
      if (t(i) equals "string") {
        schemaArray = schemaArray :+ StructField(key, StringType, true)
        types = types :+ "string"
      } else if (t(i) equals "number") {
        schemaArray = schemaArray :+ StructField(key, LongType, true)
        types = types :+ "number"
      } else if (t(i) equals "int") {
        schemaArray = schemaArray :+ StructField(key, IntegerType, true)
        types = types :+ "int"
      } else {
        schemaArray = schemaArray :+ StructField(key, StringType, true)
        types = types :+ "unknown"
      }
    }
    schema = StructType(schemaArray)
  }

  /**
   * 정의된 스키마를 불러옴
   */
  def getSchema(): StructType = {
    schema
  }

  /**
   * RDD 데이터를 정의된 스키마 형태로 Parsing함.
   *
   * 컬럼 타입(string, number)에 따라서 형변환을 함.
   *
   * RDD -> Seq -> Row 형태로 데이터가 리턴됨.
   */
  def parseRow(r: RDD[LinkedHashMap[String, String]]): RDD[Row] = {
    def convert(x: LinkedHashMap[String, String]): Seq[Any] = {
      var column: Array[Any] = Array()
      var cnt = 0

      for ((k, v) <- x) {
        if (types(cnt) equals "string") {
          column = column :+ v
        } else if (types(cnt) equals "number") {
          column = column :+ v.toLong
        } else if (types(cnt) equals "int") {
          column = column :+ v.toInt
        } else {
          column = column :+ v
        }

        cnt = cnt + 1
      }
      
      column.toSeq
    }

    r.map(s => Row.fromSeq(convert(s)))
  }
}