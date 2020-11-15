ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "1.0.0"
ThisBuild / description := "ちゃま顔じゃんけん"

resolvers ++= Seq(
  "jcenter.bintray" at "https://jcenter.bintray.com"
)

libraryDependencies ++= Seq(
  "net.dv8tion" % "JDA" % "4.1.1_101",
  "com.jagrosh" % "jda-utilities" % "3.0.3",
  "org.yaml" % "snakeyaml" % "1.14",
  "com.typesafe.akka" %% "akka-actor" % "2.6.10"
)

assemblyMergeStrategy in assembly := {
  case PathList(ps @ _*) if ps.last equalsIgnoreCase "module-info.class" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val root = (project in file("."))
  .settings(
    name := "ChamaFaceRockPaperScissors",
    assemblyOutputPath in assembly := baseDirectory.value / "target" / "build" / s"ChamaFaceRockPaperScissors-${version.value}.jar",
    mainClass in assembly := Some("net.stouma915.chamafacerockpaperscissors.ChamaFaceRockPaperScissors")
  )