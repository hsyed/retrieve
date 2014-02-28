package retrieve.FreeBase

import retrieve.FreeBase
import spray.json._
import spray.client.pipelining._
import spray.http.{HttpEntity, HttpRequest, Uri}
import scalaz._
import Scalaz._
import scala.concurrent.Future
import spray.httpx.unmarshalling.FromResponseUnmarshaller

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
}

object JsonApiQuery {
  def apply[T](r: HttpRequest) = new JsonApiQuery {
    override val req: HttpRequest = r
    override type caseType = T
  }
}

trait FreeBase {
  def baseUrl: String

  def apiKey: String = "AIzaSyBZ87EJuTOIvzF6D6oE4hOxiIJoGVXHqfY"

  def serviceApiUrlSearch: String = f"$baseUrl/search"

  def serviceApiUrlMQLRead: String = f"https://$baseUrl/freebase/v1/mqlread"

  def mkMqlUri: Uri = Uri(f"$serviceApiUrlMQLRead")

  def mkQuery(q: String) = mkMqlUri withQuery ("query" -> q)
}

object FreeBaseQueries extends FreeBase {
  val baseUrl = "www.googleapis.com"

  def tVShowQuery(q: TVShowQuery) = FreeBase.JsonApiQuery[TVShowDescriptor](
      Get(mkQuery(q.toJson.compactPrint))
  )

  def movieQuery(q: MovieQuery) = FreeBase.JsonApiQuery[MovieQuery] (
    Get(mkQuery(q.toJson.compactPrint))
  )
}


