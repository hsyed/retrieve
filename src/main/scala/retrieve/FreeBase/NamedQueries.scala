package retrieve.freebase

/**
 * Created by hassan on 03/03/2014.
 */
trait NamedQuery
trait NamedQueries {
  object OscarWinners extends NamedQuery {
    def apply(year : String) = FreeBaseQueries.movieQuery(
      Awards("Academy Awards") WITH BetweenDates(year)
    )
  }
  object CannesFestival extends NamedQuery {
    def apply(year: String) = FreeBaseQueries.movieQuery(
      Festival("Cannes Film Festival") WITH BetweenDates(year)
    )
  }
}
