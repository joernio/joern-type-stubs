package io.joern.typestubs

import io.joern.typestubs.ruby.BuiltinPackageDownloader
import io.shiftleft.codepropertygraph.generated.Languages
import scopt.OParser

object OutputFormat extends Enumeration {
  val json, mpk, zip = Value
}

final case class Config(format: OutputFormat.Value = OutputFormat.zip, languageFrontend: String = Frontend.ALL, outputDirectory: String = "./builtin_types") {

  def withFormat(value: OutputFormat.Value): Config = {
    copy(format = value)
  }

  def withLanguageFrontend(value: String): Config = {
    copy(languageFrontend = value)
  }

  def withOutputDirectory(value: String): Config = {
    copy(outputDirectory = value)
  }
}

private object Frontend {

  val ALL = "ALL"

  implicit val defaultConfig: Config = Config()

  implicit val outputFormatR: scopt.Read[OutputFormat.Value] = scopt.Read.reads(OutputFormat withName _)

  val cmdLineParser: OParser[Unit, Config] = {
    val availableFrontendLanguages = Seq(ALL, Languages.RUBYSRC, Languages.CSHARP)

    val builder = OParser.builder[Config]
    import builder.*
    OParser.sequence(
      programName("joern-type-stubs"),
      opt[OutputFormat.Value]("format")
        .action((x, c) => c.withFormat(x))
        .text(s"Format to write type-stubs to: [${OutputFormat.ValueSet().mkString(",")}]"),
      opt[String]("withLanguageFrontend")
        .action((x, c) => c.withLanguageFrontend(x))
        .validate {
          case x if availableFrontendLanguages.contains(x) => success
          case x if !availableFrontendLanguages.contains(x) =>
            failure(s"Only available languages are: [${availableFrontendLanguages.mkString(", ")}]")
        }
        .text(s"The Frontend Language to generate builtin types for, defaults to `all`: [${availableFrontendLanguages
                .mkString(",")}]".stripMargin),
      opt[String]("output")
        .action((x, c) => c.withOutputDirectory(x))
        .text("Directory for type-stubs output")
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
    if config.languageFrontend == Frontend.ALL || config.languageFrontend == Languages.RUBYSRC then
      val rubyScraper = BuiltinPackageDownloader(outputDir = config.outputDirectory, format = config.format)
      rubyScraper.run()
  }
}
