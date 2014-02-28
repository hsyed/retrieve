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
  private val default_shape = JsObject(
    "name"                  ->  JsNull,
    "type"                  ->  JsString("/film/film"),
    "initial_release_date"  ->  JsNull
  )

  implicit object MovieQueryJsonFormat extends RootJsonFormat[MovieQuery] {
    def write(q: MovieQuery) =
      q.specifier match {
        case Title(x :: Nil) => default_shape.copy(Map("name" -> JsString(x)))
        case Title(x :: xs) => throw new Exception("Multiple Titles not allowed.")
      }

    def read(value : JsValue) = ???
  }

  implicit object MovieDescriptorJsonFormat extends RootJsonFormat[MovieDescriptor] {
    def write(d: MovieDescriptor) = ???

    def read(d : JsValue) = {
      MovieDescriptor("hello", "world")
    }
  }
}





trait QueryTag
trait CanSpecify extends QueryTag

case class Actor(name : List[String])        extends CanSpecify
case class Title(name : List[String])        extends CanSpecify
case class Genre(name : List[String])        extends CanSpecify
case class Awards(name : List[String])       extends CanSpecify
case class BetweenDates(name: List[String])  extends QueryTag

trait QueryElement

case class MovieQuerySpecifier(value : CanSpecify) {
  def WITH(right: QueryTag) : MovieQuery = MovieQuery(this.value,List(right))
  def AND(right: QueryTag) : MovieQuery = WITH(right)
}

case class MovieQuery(specifier : CanSpecify, predicates : List[QueryTag]) {
  def WITH(right : QueryTag) : MovieQuery = this.copy(predicates = right :: this.predicates)
  def AND(right: QueryTag) : MovieQuery = WITH(right)
}

object MovieQueryOps {
  implicit def makeSpecifierFromTag[T <: CanSpecify](value : T) = MovieQuerySpecifier(value)
  implicit def makeQueryFromSpecifier(value : MovieQuerySpecifier) = MovieQuery(value.value,Nil)
  implicit def strToSetList(value: String) = List(value)
}


