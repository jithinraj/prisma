package cool.graph.util.gcvalueconverters

import cool.graph.gc_values._
import cool.graph.shared.models.TypeIdentifier
import cool.graph.shared.models.TypeIdentifier.TypeIdentifier
import cool.graph.util.gc_value.GCJsonConverter
import org.joda.time.{DateTime, DateTimeZone}
import org.scalactic.{Bad, Good}
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.{JsObject, JsString}

class GCJsonConverterSpec extends FlatSpec with Matchers {

  val string   = StringGCValue("{\"testValue\": 1}")
  val int      = IntGCValue(234)
  val float    = FloatGCValue(2.234324324)
  val boolean  = BooleanGCValue(true)
  val id       = GraphQLIdGCValue("2424sdfasg234222434sg")
  val datetime = DateTimeGCValue(new DateTime("2018", DateTimeZone.UTC))
  val enum     = EnumGCValue("HA")
  val json     = JsonGCValue(JsObject(Seq(("hello", JsString("there")))))

  val strings   = ListGCValue(Vector(StringGCValue("{\"testValue\": 1}"), StringGCValue("{\"testValue\": 1}")))
  val ints      = ListGCValue(Vector(IntGCValue(234), IntGCValue(234)))
  val floats    = ListGCValue(Vector(FloatGCValue(2.234324324), FloatGCValue(2.234324324)))
  val booleans  = ListGCValue(Vector(BooleanGCValue(true), BooleanGCValue(true)))
  val ids       = ListGCValue(Vector(GraphQLIdGCValue("2424sdfasg234222434sg"), GraphQLIdGCValue("2424sdfasg234222434sg")))
  val datetimes = ListGCValue(Vector(DateTimeGCValue(new DateTime("2018", DateTimeZone.UTC)), DateTimeGCValue(new DateTime("2018", DateTimeZone.UTC))))
  val enums     = ListGCValue(Vector(EnumGCValue("HA"), EnumGCValue("HA")))
  val jsons     = ListGCValue(Vector(JsonGCValue(JsObject(Seq(("hello", JsString("there"))))), JsonGCValue(JsObject(Seq(("hello", JsString("there")))))))

  val rootValue = RootGCValue(Map("test" -> strings, "test2" -> datetimes))
  val nullValue = NullGCValue

  "It should take non-list GCValues and" should "convert them to Json and back without loss" in {
    forthAndBack(string, TypeIdentifier.String, false) should be(Result.Equal)
    forthAndBack(int, TypeIdentifier.Int, false) should be(Result.Equal)
    forthAndBack(float, TypeIdentifier.Float, false) should be(Result.Equal)
    forthAndBack(boolean, TypeIdentifier.Boolean, false) should be(Result.Equal)
    forthAndBack(id, TypeIdentifier.GraphQLID, false) should be(Result.Equal)
    forthAndBack(datetime, TypeIdentifier.DateTime, false) should be(Result.Equal)
    forthAndBack(enum, TypeIdentifier.Enum, false) should be(Result.Equal)
    forthAndBack(json, TypeIdentifier.Json, false) should be(Result.Equal)

  }

  "It should take list GCValues and" should "convert them to Json and back without loss" in {
    forthAndBack(strings, TypeIdentifier.String, true) should be(Result.Equal)
    forthAndBack(ints, TypeIdentifier.Int, true) should be(Result.Equal)
    forthAndBack(floats, TypeIdentifier.Float, true) should be(Result.Equal)
    forthAndBack(booleans, TypeIdentifier.Boolean, true) should be(Result.Equal)
    forthAndBack(ids, TypeIdentifier.GraphQLID, true) should be(Result.Equal)
    forthAndBack(datetimes, TypeIdentifier.DateTime, true) should be(Result.Equal)
    forthAndBack(enums, TypeIdentifier.Enum, true) should be(Result.Equal)
    forthAndBack(jsons, TypeIdentifier.Json, true) should be(Result.Equal)
  }

  "RootValue" should "not care about type and cardinality" in {
    forthAndBack(rootValue, TypeIdentifier.String, false) should be(Result.BadError)
  }

  "Nullvalue" should "work for every type and cardinality" in {
    forthAndBack(nullValue, TypeIdentifier.String, false) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.Int, false) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.Float, false) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.Boolean, false) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.GraphQLID, false) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.DateTime, false) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.Enum, false) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.Json, false) should be(Result.Equal)
    //lists
    forthAndBack(nullValue, TypeIdentifier.String, true) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.Int, true) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.Float, true) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.Boolean, true) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.GraphQLID, true) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.DateTime, true) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.Enum, true) should be(Result.Equal)
    forthAndBack(nullValue, TypeIdentifier.Json, true) should be(Result.Equal)
  }

  def forthAndBack(input: GCValue, typeIdentifier: TypeIdentifier, isList: Boolean) = {
    val converter    = GCJsonConverter(typeIdentifier, isList)
    val forth        = converter.fromGCValue(input)
    val forthAndBack = converter.toGCValue(forth)
    println(input)
    println(forth)
    println(forthAndBack)
    forthAndBack match {
      case Good(x)    => if (x == input) Result.Equal else Result.NotEqual
      case Bad(error) => Result.BadError
    }
  }

  object Result extends Enumeration {
    val Equal, BadError, NotEqual = Value
  }
}
