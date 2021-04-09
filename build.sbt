import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

enablePlugins(JavaServerAppPackaging)
enablePlugins(JavaAppPackaging)

val scalaTestVersion = "3.0.0"

name := "spark-nlp-starter"

version := "1.1"

scalaVersion := "2.12.10"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

licenses := Seq("Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0"))

developers in ThisBuild:= List(
  Developer(id="maziyarpanahi", name="Maziyar Panahi", email="maziyar.panahi@iscpif.fr", url=url("https://github.com/maziyarpanahi")),
)

libraryDependencies ++= {
  val sparkVer = "3.0.2"
  val sparkNLP = "3.0.1"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVer % Provided,
    "org.apache.spark" %% "spark-mllib" % sparkVer % Provided,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "com.johnsnowlabs.nlp" %% "spark-nlp" % sparkNLP
  )
}

/** Disables tests in assembly */
test in assembly := {}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x if x.startsWith("NativeLibrary") => MergeStrategy.last
  case x if x.startsWith("aws") => MergeStrategy.last
  case _ => MergeStrategy.last
}

/*
* If you wish to make a Uber JAR (Fat JAR) without Spark NLP
* because your environment already has Spark NLP included same as Apache Spark
**/
//assemblyExcludedJars in assembly := {
//  val cp = (fullClasspath in assembly).value
//  cp filter {
//    j => {
//        j.data.getName.startsWith("spark-nlp")
//    }
//  }
//}