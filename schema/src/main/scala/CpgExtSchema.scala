import io.shiftleft.codepropertygraph.schema._
import overflowdb.schema.SchemaBuilder
import overflowdb.schema.Property.ValueType

object CpgSchema {
  val builder   = new SchemaBuilder(domainShortName = "Cpg", basePackage = "io.shiftleft.codepropertygraph.generated")
  val cpgSchema = new CpgSchema(builder)
  val instance     = builder.build
}
