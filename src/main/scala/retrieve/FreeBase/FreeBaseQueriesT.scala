package retrieve.freebase

import retrieve.freebase
import spray.client.pipelining._
import scalaz._
import Scalaz._
import spray.httpx.unmarshalling.FromResponseUnmarshaller
import spray.http.{HttpEntity, Uri}
import spray.http.HttpRequest
import server._
import spray.httpx.PlayJsonSupport._
import QueryDSL.MovieQuery
import play.api.libs.json._
import retrieve.freebase.MovieToJson.FromFreebase
import retrieve.moviedb
import scala.concurrent._


/**
 * Created by hassan on 27/02/2014.
 */

trait JsonApiQueryLike[T] {
  def asCase: Future[T]
  def uri : String
}

abstract class JsonApiQuery[T: FromResponseUnmarshaller] extends JsonApiQueryLike[T] {
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

  def uri = req.uri.query.get("query").getOrElse("")
}

abstract class JsonApiQueryWithCache[T: FromResponseUnmarshaller] extends JsonApiQuery[T] {
  type fromCacheT = () => Future[Option[T]]
  type toCacheT = (T) => Int

  def fromCache: fromCacheT
  def toCache : toCacheT

  override def asCase(): Future[T] = {
    fromCache() flatMap  (_ match {
      case Some(x) => future {
        x
      }
      case None =>
        super.asCase().map{ x=>
          toCache(x)
          x
        }
    })
  }
}

  //TODO Parse Error responses
  object JsonApiQuery {
    def apply[T: FromResponseUnmarshaller](r: HttpRequest) = new JsonApiQuery[T] {
      override val req: HttpRequest = r
    }

    def apply[T: FromResponseUnmarshaller] (
      r: HttpRequest,
      fromCache_ : JsonApiQueryWithCache[T]#fromCacheT,
      toCache_ : JsonApiQueryWithCache[T]#toCacheT) =
      new JsonApiQueryWithCache[T] {
        override val req: HttpRequest = r

        def fromCache = fromCache_
        def toCache = toCache_

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

    def movieQuery(q: MovieQuery, defaultReleaseDate: String = "") = {
      implicit val reader = new FromFreebase.MovieDescriptorsFromFreeBase(defaultReleaseDate)

      val g = Get(mkQuery(Json.toJson(q).toString))
      freebase.JsonApiQuery[MovieList](g)
    }

    def namedListQuery(q: MovieQuery, listName: String, listQuery: String, defaultReleaseDate: String = "")
    : JsonApiQueryLike[NamedMovieList] = {
      implicit val reader = new FromFreebase.NamedMovieListFromFreeBase(
        listName, listQuery, defaultReleaseDate)

      val queryUri = Get(mkQuery(Json.toJson(q).toString))

      val movieFromCache = () => future {
        moviedb.getNamedMovieList(listName, listQuery)
      }
      val saveMovies = (x: NamedMovieList) => x.saveDB

      freebase.JsonApiQuery[NamedMovieList](queryUri, movieFromCache,saveMovies)
    }
  }


