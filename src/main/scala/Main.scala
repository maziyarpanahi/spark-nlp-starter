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
    mpnet_lightpipeline.transform(testData).count()
    mpnet_lightpipeline.transform(testData).select("entities").show(false)

  }

  def pretrainedPipelineLD(args: Array[String]): Unit = {

    spark.sparkContext.setLogLevel("ERROR")

    val testData =
      Array(
        "A természetes nyelvfeldolgozás története általában az 1950-es években kezdődött, bár a korábbi időszakokból származó munkák is megtalálhatók. 1950-ben Alan Turing közzétett egy cikket, melynek címe: „Számítástechnika és intelligenciagépek”, és amely intelligenciakritériumként javasolta a Turing-tesztet.",
        "Geoffrey Everest Hinton é um psicólogo cognitivo britânico canadense e cientista da computação, mais conhecido por seu trabalho em redes neurais artificiais. Desde 2013, ele trabalha para o Google e a Universidade de Toronto. Em 2017, foi co-fundador e tornou-se Conselheiro Científico Chefe do Vector Institute of Toronto.")

    val pipeline = new PretrainedPipeline("detect_language_43", lang = "xx")
    println(pipeline.annotate(testData).mkString("Array(", ", ", ")"))

  }
}
