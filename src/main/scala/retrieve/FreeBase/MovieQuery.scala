package retrieve.FreeBase

import spray.json._


/**
 * Created by hassan on 28/02/2014.
 */
//[{
//"name": null,
//"type": "/film/film",
//"ns1:type": "/award/award_winning_work",
//"initial_release_date<": "2009",
//"initial_release_date>": "2008",
//"initial_release_date": null
//}]

trait MyMovieQueryProtocol extends DefaultJsonProtocol {
  private object QueryToJson {
    private val hasDatePredicate = (x:QueryTag) =>  x.isInstanceOf[BetweenDates]
    private val hasSingleDatePredicate = (_:MovieQuery).predicates.filter(hasDatePredicate).size == 1
    private val hasSingleSpecifierValue = (_:MovieQuery).specifier.name.size == 1

    trait Processor {
      protected def mkWithShape(shape : Map[String,JsValue])(values : (String,JsValue) *) = JsObject(shape ++ values.toMap)

      def validator(q: MovieQuery) : Unit
      def toJson(q: MovieQuery) : JsValue

      final def apply(q: MovieQuery) : JsValue = { validator(q); toJson(q)}
    }
    
    private val default_shape = Map(
      "name"                  ->  JsNull,
      "type"                  ->  JsString("/film/film"),
      "initial_release_date"  ->  JsNull,
      "genre"                 ->  JsArray(),
      "directed_by"           ->  JsNull,
      "subjects"              ->  JsArray(),
      "trailers"              ->  JsNull
    )

    val processTitle = new Processor {
      override def validator(x:MovieQuery) =
        if(! hasSingleSpecifierValue(x)) throw new Exception("Multiple Titles not allowed.")

      override def toJson(x:MovieQuery) = mkWithShape(default_shape)(
        "name" -> JsString(x.specifier.name.head)
      )
    }

    val processAward = new Processor {
      override def validator(x:MovieQuery) = {
        if (!hasSingleDatePredicate(x)) throw new Exception("Awards as a specifier should contain a single date predicate.")
        if(x.specifier.name.nonEmpty) throw new Exception("Awards as a specifier should be parameterless")
      }

      override def toJson(x: MovieQuery) : JsValue = ???
    }
  }

  implicit object MovieQueryJsonFormat extends RootJsonFormat[MovieQuery] {
    import QueryToJson._
    def write(q: MovieQuery) =
      q.specifier match {
        case Title(_) => processTitle(q)
        case Awards(_) => processAward(q)
        case _ => throw new Exception("senseless query")
      }

    def read(value : JsValue) = ???
  }

  implicit object MovieDescriptorJsonFormat extends RootJsonFormat[MovieDescriptor] {
    def write(d: MovieDescriptor) = ???

    def read(d : JsValue) = {
      val tl = d.asJsObject.fields("result").asJsObject
      tl.getFields("name","initial_release_date", "genre","directed_by","subjects","trailers") match {
        case Seq( JsString(name), JsString(initialReleaseDate),JsArray(genres), JsString(directedBy),JsArray(subjects),JsString(trailers)) =>
          MovieDescriptor(name,initialReleaseDate,genres.asInstanceOf[List[String]], directedBy, subjects.asInstanceOf[List[String]],trailers)
      }
    }
  }
}

trait QueryTag {
  val name : List[String]
}
trait CanSpecify extends QueryTag

case class Actor(name : List[String] )        extends CanSpecify
case class Title(name : List[String] )        extends CanSpecify
case class Genre(name : List[String] )        extends CanSpecify
case class Awards(name : List[String] = Nil ) extends CanSpecify
case class BetweenDates(name: List[String]  ) extends QueryTag

trait QueryElement

case class MovieQuerySpecifier(value : CanSpecify) {
  def WITH(right: QueryTag) : MovieQuery = MovieQuery(this.value,List(right))
  def AND(right: QueryTag) : MovieQuery = WITH(right)
}

case class MovieQuery(specifier : CanSpecify, predicates : List[QueryTag]) {
  def WITH(right : QueryTag) : MovieQuery = this.copy(predicates = right :: this.predicates)
  def AND(right: QueryTag) : MovieQuery = WITH(right)
}

trait MovieQueryOps {
  implicit def makeSpecifierFromTag[T <: CanSpecify](value : T) = MovieQuerySpecifier(value)
  implicit def makeQueryFromSpecifier(value : MovieQuerySpecifier) = MovieQuery(value.value,Nil)
  implicit def strToSetList(value: String) = List(value)
}


