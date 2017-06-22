package SparkMLExtension

/**
  * Created by victorvulovic on 6/19/17.
  */

import org.apache.spark.{SparkConf, SparkContext}

object SparkMLExtension {
  def main(args: Array[String]): SparkContext = {
    val conf = new SparkConf ()
      .setMaster("local[*]") //(args.lift(0).toString())
      .setAppName("my app") //(args.lift(1).toString())
    val sc = new SparkContext (conf)
    sc
  }
}
