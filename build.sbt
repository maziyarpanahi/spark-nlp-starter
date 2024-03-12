import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

enablePlugins(JavaServerAppPackaging)
enablePlugins(JavaAppPackaging)

val scalaTestVersion = "3.2.15"

name := "spark-nlp-starter"

version := "5.3.1"

scalaVersion := "2.12.15"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

licenses := Seq("Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0"))

ThisBuild / developers := List(
  Developer(
    id = "maziyarpanahi",
    name = "Maziyar Panahi",
    email = "maziyar.panahi@iscpif.fr",
    url = url("https://github.com/maziyarpanahi")))

// Spark NLP 5.1.0 was compiled with Scala 2.12.15 and Spark 3.3.1
// Do not change these versions unless you know what you are doing
val sparkVer = "3.4.0"
val sparkNLP = "5.3.1"

libraryDependencies ++= {
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVer % Provided,
    "org.apache.spark" %% "spark-mllib" % sparkVer % Provided,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "com.johnsnowlabs.nlp" %% "spark-nlp-silicon" % sparkNLP)
}

/** Disables tests in assembly */
assembly / test := {}

assembly / assemblyMergeStrategy := {
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
