package server

import akka.actor.Actor
import spray.http.HttpMethods
import spray.can.Http
import retrieve.freebase.{MyTVShowQueryProtocol, FreeBaseQueries, TVShowQuery}
import spray.http.{HttpEntity, Uri}
import spray.httpx.SprayJsonSupport._
import spray.http.HttpRequest
import spray.http.HttpResponse
import scala.Some
import retrieve.freebase._
import scala.util.Success
import scala.util.Failure
import spray.http.HttpHeaders.RawHeader

/**
 * Created by hassan on 28/02/2014.
 */
class TopLevelActor extends Actor {

  object implicits extends MyTVShowQueryProtocol

  import implicits._

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)

//    case r@HttpRequest(HttpMethods.GET, Uri.Path("/namedlists/cannes"), _, _, _) =>
//      val peer = sender
//
//      r.uri.query.get("year") match {
//        case Some(year) =>
//
//          println("here")
//          CannesFestival(year).asCleanJson onComplete  {
//            case Success(x) =>
//
//              peer ! HttpResponse(entity = x).withHeaders(RawHeader("Access-Control-Allow-Origin","*"))
//            case Failure(x) => println(f"failure $x")
//          }
//
//        case None => peer ! HttpResponse(entity = "supply a query")
//        case _ => println("weird")
//      }

//    case r@HttpRequest(HttpMethods.GET, Uri.Path("/namedlists/oscars"), _, _, _) =>
//      val peer = sender
//
//      r.uri.query.get("year") match {
//        case Some(year) =>
//
//          println("here")
//          OscarWinners(year).asCleanJson onComplete {
//            case Success(x) =>
//
//              peer ! HttpResponse(entity = x).withHeaders(RawHeader("Access-Control-Allow-Origin", "*"))
//            case Failure(x) => println(f"failure $x")
//          }
//
//        case None => peer ! HttpResponse(entity = "supply a query")
//        case _ => println("weird")
//      }

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
    case x@_ => sender ! HttpResponse(entity = "wrong")
  }
}


