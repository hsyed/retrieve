package retrieve.trakt

import retrieve.freebase.{NamedMovieList, MovieDescriptor}
import scala.concurrent.{Promise}
import scala.util.{Success, Failure}
import spray.httpx.UnsuccessfulResponseException
import spray.http.StatusCodes

/**
 * Created by hassan on 13/03/2014.
 */
trait MovieSummaryFromFreebaseResolver {
  implicit class Resolver(md: MovieDescriptor)(implicit api: MovieSummaryApi) {
    def getTraktMovieSummary = api.acquireResourceWithCache(md.mid,md.imdb_id)
  }


  implicit class MovieListResolver(nml: NamedMovieList)(implicit api: MovieSummaryApi) {

  }
}
