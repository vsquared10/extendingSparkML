import SparkMLExtension.SparkMLExtension
import org.apache.spark.sql.{SQLContext}

val sc = SparkMLExtension.main(Array("\"local[*]\"", "my app")) // reuses code to create a SparkContext
val sqlContext = new SQLContext(sc) // creates a SQLContext needed for DataFrames--be sure to import this
import sqlContext.implicits._//
val value = sqlContext.read.textFile("file:///Users/victorvulovic/Tweets/conor-twitterdata-1-2017-04-12-18-01-25-35b3cf72-d1d3-4462-a67a-dde73bea8c74").toDF()

value.collect.take(5).foreach(println)
