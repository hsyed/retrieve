package retrieve

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
 * Created by hassan on 14/02/2014.
 */
trait Utility {
  implicit class await[T](f : Future[T]) {
    def await = Await.result(f,20.seconds)
  }
}


