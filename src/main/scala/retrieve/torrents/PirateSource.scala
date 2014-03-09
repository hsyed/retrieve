package retrieve.torrents

import spray.client.pipelining._


import scala.collection.JavaConversions._
import spray.http.HttpHeaders.Cookie
import spray.http.HttpCookie
import scala.concurrent.Future
import extraction.{Extractor}
import org.jsoup.nodes.Element

class PirateGenericExtractor(e: Element) extends Extractor {
  type extractee  = GenericTorrent
  def exname = "pbay torrent row"

  val row = e.select("td")

  def name = fieldExtractor("name") ( row.get(1).select("a").html )

  def size = fieldExtractor("size") ( row.get(4).html.split("&nbsp;") match {
    case Array(value, tag) => (tag,value.toFloat) match {
      case ("B"   , v )  => TorrentSize(v.toLong)
      case ("KiB" , v )  => TorrentSize((v * TorrentSize.KB).toLong)
      case ("MiB" , v )  => TorrentSize((v * TorrentSize.MB).toLong)
      case ("GiB" , v )  => {
        TorrentSize((v * TorrentSize.GB).toLong)
      }
    }
    case _ => throw new Exception(f"${row.get(4).html} : error in size parsing")
  } )

  def seeders = fieldExtractor("seeders") ( Integer.parseInt(row.get(5).html) )

  def leachers = fieldExtractor("leachers") ( Integer.parseInt(row.get(6).html) )

  def magnetLink = fieldExtractor("magnetLink") ( Some(row.get(3).select("nobr a").attr("href")))

  def extract = GenericTorrent(name, size,seeders,leachers, magnetLink)
}

object PirateSource extends TorrentSource {
  def baseUrl = "pirateproxy.ca"

  def makeQueryPart(query : TorrentQuery) : String = {
    val tail : String = query.order match {
        // the last part specializes to movies
      case QueryOrder.BySeeders => "/0/7/200"
      case QueryOrder.ByWhenUploaded => "/0/99/200"
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
