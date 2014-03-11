package retrieve.freebase

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created by hassan on 08/03/2014.
 */
object MovieToJson {

  object normal {
    implicit val reader = Json.reads[MovieDescriptor]
    implicit val writer = Json.writes[MovieDescriptor]
  }

  object FromFreebase {
    def reader(defaulReleaseDate: String): Reads[MovieDescriptor] = (
      (__ \ "mid").read[String] and
        (__ \ "name").read[String] and
        (__ \ "initial_release_date").readNullable[String].map(_.getOrElse(defaulReleaseDate)) and
        (__ \ "genre").read[List[String]] and
        (__ \ "directed_by").read[List[String]] and
        (__ \ "subjects").read[List[String]] and
        (__ \ "trailers").read[List[String]] and
        (__ \ "/award/award_winning_work/awards_won").readNullable[List[JsValue]].map(
          _.getOrElse(Nil).map(x => (x \ "award" \ "name").as[String])) and
        (__ \ "/imdb/topic/title_id").read[List[String]]
      ).apply(MovieDescriptor)

    class MovieDescriptorsFromFreeBase(defaultReleaseDate: String) extends Reads[MovieList] {
      implicit val filmReader = reader(defaultReleaseDate)

      override def reads(js: JsValue) = new JsSuccess(
        MovieList(
          (js \ "result").as[List[JsValue]].map {
            x =>
              x.as[MovieDescriptor]
          })
      )
    }

    class NamedMovieListFromFreeBase(listName: String, query: String, defaultReleaseDate: String) extends Reads[NamedMovieList] {
      implicit val filmReader = reader(defaultReleaseDate)

      override def reads(js: JsValue) = {
        val entries = (js \ "result").as[List[JsValue]].map {
          x =>
            x.as[MovieDescriptor]
        }
        new JsSuccess(
          NamedMovieList(listName, query, entries)
        )
      }
    }
  }
}

