name := "chelford_ticketing"

version := "0.1.0"

scalaVersion := "2.11.8"

//libraryDependencies += "org.clapper" %% "grizzled-scala" % "2.0.0"

//Library for creating PDFs
//libraryDependencies += "org.apache.pdfbox" % "pdfbox" % "2.0.1"

libraryDependencies ++= Seq(
  //Library for creating QrCodes
  "com.github.kenglxn.QRGen" % "javase" % "2.2.0",
  //Library for creating PDFs
  "com.itextpdf" % "itextg" % "5.5.9",
  "com.itextpdf.tool" % "xmlworker" % "5.5.9",
  //Library for database interaction
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "org.postgresql" % "postgresql" % "9.4.1208.jre7",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.7.21",
  //Library for data operation
  "joda-time" % "joda-time" % "2.9.3",
  "org.joda" % "joda-convert" % "1.8.1",
  //Library for Slick joda data-time mapper
  "com.github.tototoshi" % "slick-joda-mapper" % "2.1.0",
  //Library for config
  "com.typesafe" % "config" % "1.3.0"
)

resolvers += "jitpack.io" at "https://jitpack.io"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

