package retrieve.freebase

import retrieve.freebase
import spray.json._
import spray.client.pipelining._
import scalaz._
import Scalaz._
import scala.concurrent.Future
import spray.httpx.unmarshalling.FromResponseUnmarshaller
import spray.http.{HttpEntity, Uri}
import spray.http.HttpRequest


/**
 * Created by hassan on 27/02/2014.
 */

sealed trait JsonApiQuery {
  val req: HttpRequest
  type caseType

  def doQuery[Z, T <% ((HttpRequest) => Future[Z])](p: T): Future[Z] = p(req)

  def asString(implicit um: FromResponseUnmarshaller[caseType], sh: Show[caseType]):
  Future[HttpEntity] = asCase.map(x => HttpEntity(x.show.toString()))

  def asJson: Future[HttpEntity] = doQuery(sendReceive).map(_.entity)

  def asCase(implicit unmarsh: FromResponseUnmarshaller[caseType]): Future[caseType]
  = doQuery(sendReceive ~> unmarshal[caseType])

  def awaitShow(implicit um: FromResponseUnmarshaller[caseType], sh: Show[caseType]) = asString.await.asString

  def uri = req.uri.query.get("query")
}

//TODO Parse Error responses
//HttpEntity(application/json; charset=UTF-8,{
//"error": {
//"errors": [
//{
//"domain": "global",
//"reason": "invalid",
//"message": "Unique query may have at most one result. Got 10",
//"locationType": "other",
//"location": "genre"
//}
//],
//"code": 400,
//"message": "Unique query may have at most one result. Got 10"
//}
//}
//)
object JsonApiQuery {
  def apply[T](r: HttpRequest) = new JsonApiQuery {
    override val req: HttpRequest = r
    override type caseType = T
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
      Get(mkQuery(q.toJson.compactPrint))
  )

  def movieQuery(q: MovieQuery) = {
    val g = Get(mkQuery(q.toJson.compactPrint))
   // println(q.toJson.compactPrint)
    freebase.JsonApiQuery[MovieDescriptors] (g)
  }
}


