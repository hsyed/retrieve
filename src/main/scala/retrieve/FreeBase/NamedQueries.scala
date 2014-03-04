package retrieve.freebase

/**
 * Created by hassan on 03/03/2014.
 */


trait NamedQuery
trait NamedQueries {
  object OscarWinners extends NamedQuery {
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
    def apply(year: String) = FreeBaseQueries.movieQuery(
      Festival("Cannes Film Festival") WITH BetweenDates(year)
    )
  }
}
