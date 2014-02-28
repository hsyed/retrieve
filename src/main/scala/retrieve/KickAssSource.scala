package retrieve

import com.typesafe.config.{ConfigFactory, Config}
import scala.concurrent.Future
import scala.util.{Success,Failure}

import spray.client.pipelining._

import spray.http.HttpResponse

import org.jsoup.nodes.Document
import org.jsoup.Jsoup



trait TorrentSource extends SprayClient {

  def getBlanketQueryFuture(q: String) : Future[HttpResponse]
  def extractBlanketQueryFuture(q: String) : Future[List[GenericTorrent]]

  implicit def jSoupDocumentFromHttpResponse(res : HttpResponse) : Document = Jsoup.parse(res.entity.asString)

  def makeQueryPart(query : TorrentQuery) : String

  def printBlanketQueryResult(q:String) = extractBlanketQueryFuture(q) onComplete {
    case Success(result) => result.foreach(println(_))
    case Failure(t) => println(t.getMessage)
  }
}

class KickAssSource(config: Config) extends TorrentSource {

  def this() {
    this(ConfigFactory.load())
  }
  val baseUrl = config.getString("KickassSource.baseUrl")

  def makeQueryPart(query : TorrentQuery) = query.raw

  def getBlanketQueryFuture(q: String) = {
    val query = f"/usearch/${q.replace(" ", "%20")}/"
    hostConnector.flatMap(_(Get(query)))
  }

  def getTvShowUrl(q: String) = getBlanketQueryFuture(q).onComplete {
    case Success(response) => println(response.entity.asString)
  }
  def extractBlanketQueryFuture(q: String) : Future[List[GenericTorrent]] = ???
}
