package retrieve.freebase

/**
 * Created by hassan on 11/03/2014.
 */
object QueryDSL {
  trait QueryTag {
    val name: List[String]
  }

  trait CanSpecify extends QueryTag

  case class Actor(name: List[String]) extends CanSpecify

  case class Title(name: List[String]) extends CanSpecify

  case class Genre(name: List[String]) extends CanSpecify

  case class Awards(name: List[String] = Nil) extends CanSpecify

  case class Festival(name: List[String]) extends CanSpecify

  case class ExcludeAwardWithName(name: List[String]) extends QueryTag

  case class ExcludeGenreWithName(name: List[String]) extends QueryTag

  case class BetweenDates(name: List[String]) extends QueryTag


  trait QueryElement

  case class MovieQuerySpecifier(value: CanSpecify) {
    def WITH(right: QueryTag): MovieQuery = MovieQuery(this.value, List(right))

    def AND(right: QueryTag): MovieQuery = WITH(right)
  }

  case class MovieQuery(specifier: CanSpecify, predicates: List[QueryTag]) {
    def WITH(right: QueryTag): MovieQuery = this.copy(predicates = right :: this.predicates)

    def AND(right: QueryTag): MovieQuery = WITH(right)
  }

  trait MovieQueryOps {
    implicit def makeSpecifierFromTag[T <: CanSpecify](value: T) = MovieQuerySpecifier(value)

    implicit def makeQueryFromSpecifier(value: MovieQuerySpecifier) = MovieQuery(value.value, Nil)

    implicit def strToSetList(value: String) = List(value)
  }
}
