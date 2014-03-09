package retrieve.torrents

import akka.pattern.ask
import scala.concurrent.Future
import akka.io.IO

import spray.can.Http
import spray.client.pipelining._

/**
 * Created by hassan on 27/02/2014.
 */
trait SprayClient {
  def baseUrl : String

  def hostConnector : Future[SendReceive] = for {
    Http.HostConnectorInfo(connector,_) <- IO(Http) ? Http.HostConnectorSetup(baseUrl, port = 80)
  } yield sendReceive(connector)

  def secureHostConnector = for {
    Http.HostConnectorInfo(connector,_) <- IO(Http) ? Http.HostConnectorSetup(baseUrl, port = 443, sslEncryption = true)
  } yield connector
}
