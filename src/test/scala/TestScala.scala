/**
  * Created by P088466 on 2016-07-18.
  */
import io.skiper.writer.RedisConnector
import scala.collection.JavaConversions._
object TestScala {

  def main (args: Array[String]){
    val redis = new RedisConnector("1.234.69.44")
    val blacklist = redis.getBlackList("key_blacklist")
    val vectorList = Vector.empty  :+ 5 :+ 10 :+ 50

    val list = redis.getBlackList("blacklist")

    val set = list.toSet

    set.foreach(println)


  }


}
