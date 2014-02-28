package retrieve

/**
 * Created by hassan on 14/02/2014.
 */
object Messages {
  trait TorrentQuery

  case class BlanketQuery(q: String)
}
