package retrieve


import spray.client.pipelining._


import scala.collection.JavaConversions._
import spray.http.HttpHeaders.Cookie
import spray.http.HttpCookie
import scala.concurrent.Future
import extraction.PirateGenericExtractor


class PirateSource extends TorrentSource {
  def baseUrl = "pirateproxy.ca"

  def makeQueryPart(query : TorrentQuery) : String = {
    val tail : String = query.order match {
      case QueryOrder.BySeeders => "/0/7/0"
      case QueryOrder.ByWhenUploaded => "/0/99/0"
    }

    f"/search/${query.raw.replace(" ", "%20")}$tail"
  }

  def getBlanketQueryFuture(q: String) = {
    val query = makeQueryPart(TorrentQuery(q))
    hostConnector.flatMap(_(Get(query) ~> Cookie(HttpCookie("lw","s"))))
  }

  def extractBlanketQueryFuture(q: String) : Future[List[GenericTorrent]] = {
    getBlanketQueryFuture(q).map(x=>{
      x.select("table#searchResult tbody tr").map { y=>
        new PirateGenericExtractor(y).extract
      }.toList
    })
  }
}
