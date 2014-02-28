package server

import akka.io.IO
import spray.can.Http
import akka.actor.Props

/**
 * Created by hassan on 28/02/2014.
 */
object ServerLauncher extends App {
  val tla = actorSystem.actorOf(Props[TopLevelActor],"top-level-listener")
  IO(Http) ! Http.Bind(tla, interface = "localhost", port = 8080)
}
