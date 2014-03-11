package retrieve
import scala.concurrent._
import scala.concurrent.duration._
import server._
import retrieve.freebase.QueryDSL.MovieQueryOps


/**
 * Created by hassan on 27/02/2014.
 */
package object freebase
  extends TVShowOps
  with MyTVShowQueryProtocol
  with MovieQueryOps
  with spray.httpx.SprayJsonSupport
  with MovieDescriptorOps
  with NamedQueries
  with Utility
  with MyMovieQueryProtocol
  {


}
