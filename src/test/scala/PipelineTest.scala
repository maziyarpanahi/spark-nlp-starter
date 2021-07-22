import org.scalatest.FlatSpec

class PipelineTest extends FlatSpec{
  "Spark NLP Starter" should "correctly download and annotate" in {
    Main.main(Array.empty)
  }

  "Spark NLP Starter" should "correctly work with pretrained Pipeline" in {
    Main.pretrainedPipeline(Array.empty)
  }
}
