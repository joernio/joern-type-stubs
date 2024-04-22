package io.joern.typestubs

import io.joern.typestubs.ruby.BuiltinPackageDownloader

/** Example program that makes use of Joern as a library */
object Main {
  def main(args: Array[String]): Unit = {
    val rubyScraper = BuiltinPackageDownloader()
    rubyScraper.run()
  }
}
