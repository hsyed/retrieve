package retrieve.freebase
import retrieve.freebase.QueryDSL._
import retrieve.freebase.QueryDSL.BetweenDates
import retrieve.freebase.QueryDSL.Awards
import retrieve.freebase.QueryDSL.ExcludeAwardWithName
import retrieve.freebase.QueryDSL.Festival

/**
 * Created by hassan on 03/03/2014.
 */


trait NamedQuery {
  def name : String
}

trait NamedQueries {

  object OscarWinners extends NamedQuery {
    def name : String = "OscarWinners"

    def apply(year : String) = FreeBaseQueries.movieQuery(
      Awards("Academy Awards")
        WITH BetweenDates(year)
        WITH ExcludeAwardWithName(List(
          "Academy Award for Best Short Film (Animated)",
          "Academy Award for Best Documentary Short Subject",
          "Academy Award for Best Short Film (Live Action)"))
    )
  }
  object CannesFestival extends NamedQuery {
    def name : String = "CannesFestival"
    def query(year: String) = (
      Festival("Cannes Film Festival")
      WITH BetweenDates (year)
      WITH ExcludeGenreWithName (List(
        "Short Film"
      ))
    )

    def apply(year: String) = FreeBaseQueries.movieQuery(
      query(year)
    )
  }
}
