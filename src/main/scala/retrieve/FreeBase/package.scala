package retrieve

/**
 * Created by hassan on 27/02/2014.
 */
package object FreeBase extends TVShowOps with MyTVShowQueryProtocol with MyMovieQueryProtocol {
  implicit val as =  server.actorSystem //AkkaSystem.actorSystem
  implicit val as_d = server.actorSystem.dispatcher

}
