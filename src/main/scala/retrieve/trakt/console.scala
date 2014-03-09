package retrieve.trakt

import com.typesafe.config.{ConfigFactory, Config}

/**
 * Created by hassan on 04/03/2014.
 */

trait TraktUser {
  def apiKey: String

  def username: String

  def passwordSha1: String

  def mkHash(in: String): String = {
    java.security.MessageDigest.getInstance("SHA-1").digest(in.getBytes()).map("%02X".format(_)).mkString
  }
}

class TraktUserFromConfig(config : Config) extends TraktUser {
  def this() {
    this(ConfigFactory.load())
  }

  def apiKey = config.getString("TraktUser.apiKey")
  def username = config.getString("TraktUser.username")
  def passwordSha1 = mkHash(config.getString("TraktUser.password"))

}

object console {
  implicit object defaultUser extends TraktUserFromConfig

}
