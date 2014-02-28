package retrieve

object QueryOrder extends Enumeration {
  type QueryOrder = Value
  val ByWhenUploaded, BySeeders = Value
}
import QueryOrder._

/**
 * Created by hassan on 15/02/2014.
 */
class TorrentQuery(val raw : String, val order : QueryOrder) {

}

object TorrentQuery {
  def apply(raw: String) = new TorrentQuery(raw,BySeeders)
}
