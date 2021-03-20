import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.Pipeline

import com.johnsnowlabs.nlp.base._
import com.johnsnowlabs.nlp.annotator._

object Main {
  def main(args: Array[String]) {
    val spark: SparkSession = SparkSession.builder
      .appName("spark-nlp-starter")
      .master("local[*]")
      .getOrCreate

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

    val posTagger = PerceptronModel.pretrained()
      .setInputCols("sentence", "token")
      .setOutputCol("pos")

    val wordEmbeddings = WordEmbeddingsModel.pretrained()
      .setInputCols("sentence", "token")
      .setOutputCol("word_embeddings")

    val ner = NerDLModel.pretrained("ner_dl","en")
      .setInputCols("token", "sentence","word_embeddings")
      .setOutputCol("ner")

    val nerConverter = new NerConverter()
      .setInputCols("sentence", "token", "ner")
      .setOutputCol("ner_converter")

    val finisher = new Finisher()
      .setInputCols("ner", "ner_converter")
      .setCleanAnnotations(false)

    val pipeline = new Pipeline().setStages(
      Array(
        document,
        sentenceDetector,
        token,
        posTagger,
        wordEmbeddings,
        ner,
        nerConverter,
        finisher))

    val testData = spark.createDataFrame(Seq(
      (1, "Google has announced the release of a beta version of the popular TensorFlow machine learning library"),
      (2, "The Paris metro will soon enter the 21st century, ditching single-use paper tickets for rechargeable electronic cards.")
    )).toDF("id", "text")

    val predicion = pipeline.fit(testData).transform(testData)
    predicion.select("ner_converter.result").show(false)
    predicion.select("pos.result").show(false)

  }
}
