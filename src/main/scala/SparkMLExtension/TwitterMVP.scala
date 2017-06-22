package SparkMLExtension

/**
  * Created by victorvulovic on 6/19/17.
  */

import org.apache.spark.sql.SQLContext
import org.apache.spark.ml.feature.{HashingTF, IDF, RegexTokenizer}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import scala.util.parsing.json.JSON

object TwitterMVP {
  def main(args: Array[String]) {

    val sc = SparkMLExtension.main(args) // reuses code to create a SparkContext
    val sqlContext = new SQLContext(sc) // creates a SQLContext needed for DataFrames--be sure to import this
    val value = sqlContext.read.textFile("file:///Users/victorvulovic/Tweets/conor-twitterdata-1-2017-04-12-18-01-25-35b3cf72-d1d3-4462-a67a-dde73bea8c74").toDF("value")
    val parsed = new tweetParser().setInputCol("value").setOutputCol("tweet").setOutputCol2("lang")
    val tokenizer = new RegexTokenizer().setInputCol("value").setOutputCol("words").setPattern("\\ s +|[,.\"]")
    val hashingTF = new HashingTF().setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(200)
    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val forestizer = new RandomForestClassifier().setLabelCol("lang").setFeaturesCol("features").setNumTrees(10)
    val pipeline = new Pipeline().setStages(Array(parsed, tokenizer, hashingTF, idf, forestizer))
    val Array(tweets_train, tweets_test) = value.randomSplit(Array(0.7, 0.3))
    val model = pipeline.fit(tweets_train)
    val test_model = model.transform(tweets_test)

    val evaluator = new BinaryClassificationEvaluator().setRawPredictionCol("probability").setLabelCol("lang")
    def printlnMetric(metricName: String): Unit = {
      println(metricName + " = " + evaluator.setMetricName(metricName).evaluate(test_model))
    }

    printlnMetric("areaUnderROC")

    sc.stop()
  }
}