package retrieve.freebase

import spray.json._
import scala.util.matching._
import scalaz._
import Scalaz._


/**
 * Created by hassan on 28/02/2014.
 */

//[{
//"type": "/film/film",
//"directed_by": [],
//"name": null,
//"trailers": [],
//"genre": [],
//"subjects": [],
//"film_festivals": [{
//"festival": {
//"name": "cannes film festival"
//}
//}]
//}]
trait MyMovieQueryProtocol extends DefaultJsonProtocol {

  private object QueryToJson {
    private val isDatePredicate = (x: QueryTag) => x.isInstanceOf[BetweenDates]
    private val hasSingleYearPredicate = (x: MovieQuery) => {
      val dates = x.predicates.filter(isDatePredicate)
      if (dates.size != 1)
        false
      else !dates.exists {
        x =>
          if (x.name.size > 2)
            true
          else x.name.exists {
            z =>
              """\d\d\d\d""".r.findFirstIn(z.trim) match {
                case Some(_) => false
                case _ => true
              }
          }
      }
    }

    private val hasSingleSpecifierValue = (_: MovieQuery).specifier.name.size == 1

    trait Processor {
      protected def mkWithShape(shape: Map[String, JsValue])(values: (String, JsValue)*) = JsArray(JsObject(shape ++ values.toMap))

      protected def mkDatePredicate(q: MovieQuery): List[(String, JsValue)] = q.predicates.filter(isDatePredicate).head match {
        case BetweenDates(x :: Nil) => List("initial_release_date" -> JsString(x))
        case BetweenDates(x :: y :: Nil) =>
          val l = "initial_release_date>"
          val r = "initial_release_date<"
          if (Integer.parseInt(x) < Integer.parseInt(y)) List(l -> JsString(x), r -> JsString(y))
          else List(r -> JsString(x), l -> JsString(y))
      }

      def validator(q: MovieQuery): Unit

      def toJson(q: MovieQuery): JsValue

      final def apply(q: MovieQuery): JsValue = {
        validator(q); toJson(q)
      }
    }

    private val default_shape = Map(
      "name" -> JsNull,
      "type" -> JsString("/film/film"),
      "initial_release_date" -> JsNull,
      "genre" -> JsArray(),
      "directed_by" -> JsArray(),
      "subjects" -> JsArray(),
      "trailers" -> JsArray(),
      "/award/award_winning_work/awards_won" -> JsArray(JsObject(
        "award" -> JsNull,
        "ceremony" -> JsNull,
        "year" -> JsNull
      ))
    )

    val processTitle = new Processor {
      override def validator(x: MovieQuery) =
        if (!hasSingleSpecifierValue(x)) throw new Exception("Multiple Titles not allowed.")

      override def toJson(x: MovieQuery) = mkWithShape(default_shape)(
        "name" -> JsString(x.specifier.name.head)
      )
    }

    val processAward = new Processor {
      override def validator(x: MovieQuery) = {
        if (!hasSingleYearPredicate(x)) throw new Exception("Awards as a specifier should contain a single date predicate, and this predicate should be a year or a year range.")
        if (x.specifier.name.nonEmpty) throw new Exception("Awards as a specifier should be parameterless.")
      }

      override def toJson(x: MovieQuery): JsValue = mkWithShape(default_shape)(
        "x:type" -> JsString("/award/award_winning_work"),
        "y:/award/award_winning_work/awards_won" -> JsArray(JsObject(
          "award" -> JsArray(JsObject(
            "category_of" -> JsString("Academy Awards")
          )),
          "year" -> JsString(x.predicates.filter(isDatePredicate).head.name.head)
        ))
      )
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

    def read(value: JsValue) = ???
  }

  implicit object MovieDescriptorJsonFormat extends RootJsonFormat[MovieDescriptors] {
    def write(d: MovieDescriptors) = ???

    def extractMD(tl : JsObject) : MovieDescriptor = {
      tl.getFields("name", "initial_release_date", "genre", "directed_by", "subjects", "trailers") match {
        case Seq(JsString(name), JsString(release_date), JsArray(genre), JsArray(directed_by), JsArray(subjects), JsArray(trailers)) =>
          MovieDescriptor(name, release_date, genre.asInstanceOf[List[String]],directed_by.asInstanceOf[List[String]],subjects.asInstanceOf[List[String]], trailers.asInstanceOf[List[String]])
      }
    }

    def read(d: JsValue): MovieDescriptors = {
       val tlll = d.asJsObject.fields("result").asInstanceOf[JsArray]
          val mdl = tlll.elements.map {tl =>extractMD(tl.asJsObject)}.toList
          MovieDescriptors(mdl)
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


