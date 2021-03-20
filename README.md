# spark-nlp-starter
 
![Scala CI](https://github.com/maziyarpanahi/spark-nlp-starter/workflows/Scala%20CI/badge.svg)

This is just a simple demo as how to use Spark NLP in a SBT project in Scala. Usually, once you have your application you want to package it and run it via `spark-submit`.

## spark-submit
After your executed `sbt assembly` to get a Fat JAR (without Apache Spark since your environment has Apache Spark already), you can use `spark-submit` like this:

```shell
~/spark-3.1.1-bin-hadoop3.2/bin/spark-submit \
--class "Main" \
target/scala-2.12/spark-nlp-starter-assembly-1.1.jar
```

This will execute the code in `Main` class and finish successfully.