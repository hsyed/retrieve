package retrieve.moviedb
import MyPostgresDriver.simple._
import retrieve.freebase.MovieDescriptor
import play.api.libs.json.{Json, JsValue}

/**
 * Created by hassan on 10/03/2014.
 */

object Schema {
  class DBMovieDescriptor(tag: Tag) extends Table[MovieDescriptor](tag, "MovieDescriptor") {
    import retrieve.freebase.MovieToJson.normal._
    def mID = column[String]("m_id", O.PrimaryKey)
    def data = column[JsValue]("data")

    def * = (mID,data).shaped <> (
      (d: (String,JsValue)) => d._2.as[MovieDescriptor],
      (md:MovieDescriptor) => Some((md.mid, Json.toJson(md)))
    )
}

  val dbMovieDescriptor = TableQuery[DBMovieDescriptor]
}
