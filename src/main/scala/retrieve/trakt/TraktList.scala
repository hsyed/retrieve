package retrieve.trakt

import spray.client.pipelining._
import scala.Some
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.http._
import spray.httpx.marshalling._


import DefaultJsonProtocol._
import retrieve.trakt.TraktGet.TraktUserListResponse
import retrieve.freebase.MovieDescriptors
import retrieve.freebase.MovieDescriptors
import retrieve.trakt.TraktGet.TraktUserListResponse
import scala.Some

/**
 * Created by hassan on 04/03/2014.
 */


object TraktGet {
  case class TraktUserListResponse(name : String, slug : String, url : String, description : String,
                                   privacy : String, show_numbers : Boolean, allow_shouts : Boolean)
  implicit val TraktUserListFormat = jsonFormat7(TraktUserListResponse)

  def userList(implicit user : TraktUser) = {
    val uri = Uri(f"http://api.trakt.tv/user/lists.json/${user.apiKey}/${user.username}")
    val pipeline = sendReceive ~> unmarshal[Seq[TraktUserListResponse]]
    pipeline(Get(uri))
  }
}

object TraktPost {
  case class TraktListAddCommand(name : String, username : String, password : String, privacy : String = "public")
  implicit val TraktListAddCommandFormat = jsonFormat4(TraktListAddCommand)

  def addList(name : String) (implicit user : TraktUser) = {
    val uri = f"http://api.trakt.tv/lists/add/${user.apiKey}"
    val pipeline = sendReceive
    val p = Post(uri,TraktListAddCommand(name,user.username,user.passwordSha1))
    pipeline(p).await
  }

  def addAllDescriptors(name : String, movies : MovieDescriptors)(implicit user : TraktUser)  = {
    val uri = f"http://api.trakt.tv/lists/items/add/${user.apiKey}"
    println(uri)
    val moviesAsJson = JsArray(
      movies.md.flatMap{
        x=> println(x.imdb_id)
          x.imdb_id.map( z=> {
            println(x.title)
            println(z)
            JsObject(
          "type" -> JsString("movie"),
          "imdb_id" -> JsString(z),
          "title" -> JsString(x.title),
          "year" -> JsNumber(x.initialReleaseDate.substring(0,4).toInt)
    )})})
    val postBody = JsObject (
      "username" -> JsString(user.username),
      "password" -> JsString(user.passwordSha1),
      "slug" -> JsString(name),
      "items" -> moviesAsJson
    )
    println(postBody)
    val pipeline = sendReceive
    val p = Post(uri, postBody)
    println(p)
    pipeline(p).await
  }
}


class TraktList(slug : String) {
  def update(implicit user : TraktUser) = ???
  def delete(implicit user : TraktUser)= ???
}

object TraktList {
  private var lists_ : Option[Map[String, TraktUserListResponse]] = None

  def lists (implicit user : TraktUser) = lists_ match {
    case None =>
      lists_ = Some(updateLists)
      lists_.get
    case Some(list) => list
  }

  private def updateLists (implicit user : TraktUser) : Map[String, TraktUserListResponse] =
    Map(TraktGet.userList.await.map(x=> (x.name, x)) : _*)

  def add(name: String) (implicit user : TraktUser) = lists.get(name) match {
      case Some(x) => throw new Exception(f"List $name allready exists")
      case None => TraktPost.addList(name)
      updateLists
    }

  def addAllDescriptors(name: String, movies : MovieDescriptors)
  (implicit user : TraktUser) = {
    val slug = lists.apply(name).slug
    TraktPost.addAllDescriptors(slug,movies)
  }


  def delete(slug: String)
    (implicit user : TraktUser) = ???
}
