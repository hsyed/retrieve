package server

import akka.actor.Actor
import spray.http.HttpMethods
import spray.can.Http
import retrieve.FreeBase.{MyTVShowQueryProtocol, FreeBaseQueries, TVShowQuery}
import spray.http.{HttpEntity, Uri}
import spray.httpx.SprayJsonSupport._
import spray.http.HttpRequest
import spray.http.HttpResponse
import scala.Some

/**
 * Created by hassan on 28/02/2014.
 */
class TopLevelActor extends Actor {

  object implicits extends MyTVShowQueryProtocol

  import implicits._

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)

    case r@HttpRequest(HttpMethods.GET, Uri.Path("/mql/tv"), _, _, _) =>
      val peer = sender
      val query = r.uri.query.get("q")
      val format = r.uri.query.get("f").getOrElse("s")

      query match {
        case Some(q) =>
          val fut = format match {
            case "j" => FreeBaseQueries.tVShowQuery(TVShowQuery(q)).asJson
            case "s" => FreeBaseQueries.tVShowQuery(TVShowQuery(q)).asString
          }
          fut onSuccess {
            case x: HttpEntity => peer ! HttpResponse(entity = x)
          }
        case None => peer ! HttpResponse(entity = "supply a query!")
      }
    case x@_ => println(x)
  }
}


