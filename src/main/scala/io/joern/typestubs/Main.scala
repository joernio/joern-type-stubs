package io.joern.typestubs

import io.joern.typestubs.ruby.BuiltinPackageDownloader
import scopt.OParser

final case class Config(writeToJson: Boolean = false, languageFrontend: String = "all") {

  def writeToJson(value: Boolean): Config = {
    copy(writeToJson = value)
  }

  def withLanguageFrontend(value: String): Config = {
    copy(languageFrontend = value)
  }
}

private object Frontend {

  implicit val defaultConfig: Config = Config()

  val cmdLineParser: OParser[Unit, Config] = {
    val availableFrontendLanguages = Seq("", "ruby", "chsarp", "all")

    val builder = OParser.builder[Config]
    import builder.*
    OParser.sequence(
      programName("joern-type-stubs"),
      opt[Unit]("writeToJson")
        .action((_, c) => c.writeToJson(true))
        .text("Write builtin types to JSON files without zipping, instead of MessagePack files with a zipped folder"),
      opt[String]("withLanguageFrontend")
        .action((x, c) => c.withLanguageFrontend(x))
        .validate {
          case x if availableFrontendLanguages.contains(x) => success
          case x if !availableFrontendLanguages.contains(x) =>
            failure(s"Only available languages are: [${availableFrontendLanguages.mkString(", ")}]")
        }
        .text("""The Frontend Language to generate builtin types for, defaults to `all`:
            |ruby -> Ruby
            |csharp -> C#
            |all -> All of the above languages
            |""".stripMargin)
    )
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    OParser.parse(Frontend.cmdLineParser, args, Config()) match {
      case Some(config) =>
        try {
          run(config)
        } catch {
          case ex: Throwable =>
            println(ex.getMessage)
            ex.printStackTrace()
            System.exit(1)
        }
      case None =>
        println("Error parsing the command line")
        System.exit(1)
    }
  }

  def run(config: Config): Unit = {
    println(config)
    if config.languageFrontend == "all" || config.languageFrontend == "ruby" then
      val rubyScraper = BuiltinPackageDownloader(writeToJson = config.writeToJson)
      rubyScraper.run()
  }
}
