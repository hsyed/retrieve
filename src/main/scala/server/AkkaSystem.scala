import akka.actor.ActorSystem
import scala.concurrent.duration._

/**
 * Created by hassan on 14/02/2014.
 */
package object server {
  implicit val actorSystem = ActorSystem()
  implicit val dispatcher = actorSystem.dispatcher

  implicit val defaultTimeout : akka.util.Timeout = 20.seconds
}
