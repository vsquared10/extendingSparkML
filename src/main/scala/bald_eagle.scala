/**
  * Created by victorvulovic on 6/19/17.
  */

import org.apache.spark.{SparkConf, SparkContext}

object bald_eagle {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("CountingSheep")
    val sc = new SparkContext(conf)
    val test = sc.parallelize(1 to 1000).sum
    println("This is the test result " + test)
  }
}
