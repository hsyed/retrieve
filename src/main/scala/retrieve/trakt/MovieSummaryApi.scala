package retrieve.trakt


import spray.client.pipelining._
import spray.httpx.PlayJsonSupport._
import play.api.libs.json._
import scala.concurrent._
import scala.util.{Success,Failure}
import spray.httpx.UnsuccessfulResponseException
import spray.http.StatusCodes
import retrieve.moviedb.TraktInterface

/**
 * Created by hassan on 12/03/2014.
 */
case class TraktMovieSummary(
                              mid: Option[String],
                              title: String,
                              year: Int,
                              released: Int,
                              url: String,
                              trailer: String,
                              runtime: Int,
                              tagline: String,
                              overview: String,
                              certification: String,
                              imdb_id: String,
                              tmdb_id: Int,
                              rt_id: Int,
                              last_updated: Int,
                              images: Map[String, String],
                              genres: List[String],
                              ratings: Map[String, String]
                              )

class MovieSummaryApi(implicit user: TraktUser) {
  // force a Map[String,X] into a Map[String,String]
  implicit val readsMaps = new Reads[Map[String, String]] {
    override def reads(value: JsValue) = JsSuccess(
      value.as[Map[String, JsValue]].map(x => (x._1 -> x._2.toString()))
    )
  }

  implicit val readsSummary = Json.reads[TraktMovieSummary]

  val baseUri = f"http://api.trakt.tv/movie/summary.json/${user.apiKey}"

  def mkQuery(title: String) = Get(f"$baseUri/$title")

  def acquireResource(title: String): Future[TraktMovieSummary] = {
    val pipeline = sendReceive ~> unmarshal[TraktMovieSummary]
    pipeline(mkQuery(title))
  }


  def acquireResourceWithCache(freebase_id: String, titles: List[String]) = {
    val promise = Promise[Option[TraktMovieSummary]]
    val fut = future { TraktInterface.get(freebase_id) }

    fut onSuccess {
      case None =>
        helper.multiFind(freebase_id,titles,this) onSuccess {
          case x @ Some(_) =>
            TraktInterface.put(x.get)
            promise success x
          case None => promise success None
        }
      case x@ Some(_) => promise success x
    }

    promise.future
  }
}


private object helper {
  def multiFind(freebase_id : String, titles: List[String], api: MovieSummaryApi) = {
    val result = Promise[Option[TraktMovieSummary]]

    def recurseAndFindMovie(lst: List[String]): Unit =
      api.acquireResource(lst.head) onComplete {
        case Success(x) => result success Some(x.copy(mid = Some(freebase_id)))
        case Failure(f) =>
          if (isMovieNotFoundError(f)) recurseOrTerminate(lst)
          else result.failure(f)
      }

    def recurseOrTerminate(lst: List[String]) =
      if (lst.tail.isEmpty) result success None
      else recurseAndFindMovie(lst.tail)

    def isMovieNotFoundError(f: Throwable) =
      f.isInstanceOf[UnsuccessfulResponseException] &&
        f.asInstanceOf[UnsuccessfulResponseException].response.status == StatusCodes.NotFound

    recurseAndFindMovie(titles)
    result.future
  }
}