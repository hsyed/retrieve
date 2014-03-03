package retrieve
import scala.concurrent._
import scala.concurrent.duration._


/**
 * Created by hassan on 27/02/2014.
 */
package object freebase
  extends TVShowOps
  with MyTVShowQueryProtocol
  with MyMovieQueryProtocol
  with MovieQueryOps
  with spray.httpx.SprayJsonSupport
  with MovieDescriptorOps
  with NamedQueries
  {
  implicit val as =  server.actorSystem //AkkaSystem.actorSystem
  implicit val as_d = server.actorSystem.dispatcher

  implicit class await[T](f : Future[T]) {
    def await = Await.result(f,5.seconds)
  }
}
