import sbt.*

object Projects {
  lazy val schema = project.in(file("schema"))
  lazy val domainClasses = project.in(file("domain-classes"))
}
