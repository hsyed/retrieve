package retrieve
/**
 * Created by hassan on 04/03/2014.
 */

package object trakt
  extends Utility {
  implicit val as = server.actorSystem
  implicit val asd = server.dispatcher
  implicit val to = server.defaultTimeout
}
