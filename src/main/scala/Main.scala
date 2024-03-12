import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.nlp.base._
import com.johnsnowlabs.nlp.embeddings.MPNetEmbeddings
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.SparkSession

object Main {
  val spark: SparkSession = SparkSession.builder
    .appName("spark-nlp-starter")
    .master("local[*]")
    .getOrCreate

  def main(args: Array[String]): Unit = {

    spark.sparkContext.setLogLevel("ERROR")

    val document = new DocumentAssembler()
      .setInputCol("text")
      .setOutputCol("document")

    val sentenceDetector = new SentenceDetector()
      .setInputCols("document")
      .setOutputCol("sentence")

    val token = new Tokenizer()
      .setInputCols("sentence")
      .setOutputCol("token")

    val posTagger = PerceptronModel
      .pretrained()
      .setInputCols("sentence", "token")
      .setOutputCol("pos")

    val wordEmbeddings = WordEmbeddingsModel
      .pretrained()
      .setInputCols("sentence", "token")
      .setOutputCol("word_embeddings")

    val pipeline = new Pipeline().setStages(
      Array(document, sentenceDetector, token, posTagger, wordEmbeddings))

    val testData = spark
      .createDataFrame(
        Seq(
          (1, "Google has announced the release of a beta version of the popular TensorFlow machine learning library"),
          (2, "The Paris metro will soon enter the 21st century, ditching single-use paper tickets for rechargeable electronic cards.")))
      .toDF("id", "text")

    val prediction = pipeline.fit(testData).transform(testData)
    prediction.select("pos.result").show(false)

  }

  def pretrainedPipeline(args: Array[String]): Unit = {

    spark.sparkContext.setLogLevel("ERROR")

    val testData = spark
      .createDataFrame(
        Seq(
          (1, "Google has announced the release of a beta version of the popular TensorFlow machine learning library"),
          (2, "The Paris metro will soon enter the 21st century, ditching single-use paper tickets for rechargeable electronic cards.")))
      .toDF("id", "text")

    val document = new DocumentAssembler()
      .setInputCol("text")
      .setOutputCol("document")

    val embeddings = MPNetEmbeddings
      .pretrained()
      .setInputCols(Array("document"))
      .setOutputCol("mpnet")

    val pipeline = new Pipeline().setStages(Array(document, embeddings))
    val mpnet_lightpipeline = new LightPipeline(pipeline.fit(testData))
    mpnet_lightpipeline.annotate(
      "Google has announced the release of a beta version of the popular TensorFlow machine learning library")
    mpnet_lightpipeline.transform(testData).select("mpnet").count()

  }

  def pretrainedPipelineLD(args: Array[String]): Unit = {

    spark.sparkContext.setLogLevel("ERROR")

    // test dataset for sentiment analysis
    val testSentimentData =
      Array(
        "The movie I watched was not good",
        "The movie I watched was not bad",
        "The movie I watched was not good, it was great",
        "The movie I watched was not bad, it was terrible",
        "The movie I watched was not good, it was terrible",
        "The movie I watched was not bad, it was great")

    val pipeline = new PretrainedPipeline("analyze_sentiment", lang = "en")
    println(pipeline.annotate(testSentimentData).mkString("Array(", ", ", ")"))

  }
}
