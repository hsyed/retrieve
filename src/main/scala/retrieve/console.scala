package retrieve

import retrieve.torrents.MovieUtils

/**
 * Created by hassan on 07/03/2014.
 */
object console
  extends Utility
  with MovieUtils
  with trakt.console {
  type TraktMovieSummary = trakt.TraktMovieSummary
  val PirateSource = torrents.PirateSource
  val CannesWinners = freebase.CannesFestival
  val OscarWinners = freebase.OscarWinners

  import retrieve.torrents._
}
