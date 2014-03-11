package retrieve.freebase

import retrieve.freebase
import spray.client.pipelining._
import scalaz._
import Scalaz._
import scala.concurrent.Future
import spray.httpx.unmarshalling.FromResponseUnmarshaller
import spray.http.{HttpEntity, Uri}
import spray.http.HttpRequest
import server._
import spray.httpx.PlayJsonSupport._
import QueryDSL.MovieQuery
import play.api.libs.json._
import spray.json._


/**
 * Created by hassan on 27/02/2014.
 */

abstract class JsonApiQuery[T : FromResponseUnmarshaller] {
  val req: HttpRequest


  def doQuery[Z, X <% ((HttpRequest) => Future[Z])](p: X): Future[Z]
    = p(req)

  def asString(implicit sh: Show[T]): Future[HttpEntity]
    = asCase.map(x => HttpEntity(x.show.toString()))

  def asJson: Future[HttpEntity]
    = doQuery(sendReceive).map(_.entity)

//  def asCleanJson(implicit sh: Show[T])
//    : Future[HttpEntity] = asCase.map( x=> {
//    println(x.toJson.compactPrint)
//    HttpEntity(x.toJson.prettyPrint)
//  })

  def asCase(): Future[T]
    = doQuery(sendReceive ~> unmarshal[T])

  def awaitShow(implicit sh: Show[T]) = asString.await.asString

  def uri = req.uri.query.get("query")
}

//TODO Parse Error responses
object JsonApiQuery {
  def apply[T : FromResponseUnmarshaller](r: HttpRequest) = new JsonApiQuery[T] {
    override val req: HttpRequest = r
  }
}

trait FreeBaseQueriesT {
  def baseUrl: String

  def serviceApiUrlSearch: String = f"$baseUrl/search"

  def serviceApiUrlMQLRead: String = f"https://$baseUrl/freebase/v1/mqlread"

  def mkMqlUri: Uri = Uri(f"$serviceApiUrlMQLRead")

  def mkQuery(q: String) = mkMqlUri withQuery ("query" -> q)
}

object FreeBaseQueries extends FreeBaseQueriesT {
  val baseUrl = "www.googleapis.com"

  def tVShowQuery(q: TVShowQuery) = freebase.JsonApiQuery[TVShowDescriptor](
      Get(mkQuery("TODO : REMOVE ME"))
  )

  def movieQuery(q: MovieQuery) = {
    import MovieToJson.FromFreebase._
    println(q)
    println(Json.toJson(q))
    val g = Get(mkQuery(Json.toJson(q).toString))
    freebase.JsonApiQuery[NamedMovieList] (g)
  }
}


