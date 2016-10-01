package io.skiper.sql

import org.apache.spark.SparkContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.hive.thriftserver._

/**
 * JDBC를 통해 SQL을 수행하기 위한 Hive Thrift Server의 구동을 담당하는 Singleton Class
 * 
 * Spark 의 configuration 디렉토리에 있는 hive-site.xml의 parameter을 읽어옴.
 * 
 * hive.metastore.uris : 메타스토어 위치 (Default : derby DB)
 */
object HiveContextSingleton {
  @transient private var instance: HiveContext = _

  /**
   * Streaming 수행중인 SparkContext를 넘겨 받아 지정된 port로 Hive Thrift Server를 구동.
   * 
   * singleton으로 HiveContext 인스턴스를 리턴함.
   */
  def getInstance(sparkContext: SparkContext, port: String): HiveContext = {
    if (instance == null) {
      instance = new HiveContext(sparkContext)
      instance.setConf("hive.server2.thrift.port", port)
      HiveThriftServer2.startWithContext(instance)
    }
    instance
  }
}
