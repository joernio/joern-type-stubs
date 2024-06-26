name := "standalone-schema"

libraryDependencies += "io.shiftleft" %% "overflowdb-codegen" % "2.104"
libraryDependencies += "io.shiftleft" %% "codepropertygraph-schema" % Versions.cpg

lazy val generatedSrcDir = settingKey[File]("root for generated sources - we want to check those in")
enablePlugins(OdbCodegenSbtPlugin)
generateDomainClasses / classWithSchema := "CpgSchema$"
generateDomainClasses / fieldName := "instance"
generateDomainClasses/outputDir       := (Projects.domainClasses / generatedSrcDir).value
