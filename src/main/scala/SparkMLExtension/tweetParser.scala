package SparkMLExtension

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.feature.{HashingTF, IDF, RegexTokenizer}
import org.apache.spark.ml.param.{Param, ParamMap}
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.{DataFrame, Dataset, SQLContext}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.functions.{col, udf}

import scala.util.parsing.json.JSON

/**
  * Created by victorvulovic on 6/21/17.
  */
class tweetParser(override val uid: String) extends Transformer {

  final val inputCol= new Param[String](this, "inputCol", "The input column")
  final val outputCol = new Param[String](this, "outputCol", "The output column")
  final val outputCol2 = new Param[String](this, "2nd inputCol", "the second input");

  def setInputCol(value: String): this.type = set(inputCol, value)
  def setOutputCol(value: String): this.type = set(outputCol, value)
  def setOutputCol2(value: String): this.type = set(outputCol2, value)

  def this() = this(Identifiable.randomUID("tweetParser"))

  def copy(extra: ParamMap): tweetParser = {
    defaultCopy(extra)
  }

  override def transformSchema(schema: StructType): StructType = {
    val idx = schema.fieldIndex($(inputCol))
    val field = schema.fields(idx)
    if (field.dataType != StringType) {
      throw new Exception(s"Input type ${field.dataType} did not match input type StringType")
    }
    schema.add(StructField($(outputCol), StringType, false))
    schema.add(StructField($(outputCol2), IntegerType, false))
  }

  def findVal(str: String, ToFind: String): String = {
    try {
      JSON.parseFull(str) match {
        case Some(m: Map[String, String]) => m(ToFind)
      }
    } catch {
      case e: Exception => null
    }
  }

  def getTweetsAndLang(input: String): (String, Int) = {
    try {
      var result = (findVal(input, "text"), -1)

      if (findVal(input, "lang") == "en") result.copy(_2 = 0)
      else if (findVal(input, "lang") == "es") result.copy(_2 = 1)
      else result
    } catch {
      case e: Exception => ("unknown", -1)
    }
  }

  def transform(df: Dataset[_]): DataFrame = {

    val textfinder = udf { in: String => findVal(in, "text") }
    val langfinder = udf { in: String => getTweetsAndLang(in)._2 }

    df.select(col("*"),
      textfinder(df.col($(inputCol))).as($(outputCol)),
      langfinder(df.col($(inputCol))).as($(outputCol2)))
      .where("tweet is not null AND lang > -1")
    }
}