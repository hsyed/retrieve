/**
 * Created by hassan on 28/02/2014.
 */
package object retrieve {
  implicit val as =  server.actorSystem //AkkaSystem.actorSystem
  implicit val as_d = server.actorSystem.dispatcher
  implicit val timout = server.defaultTimeout
}
