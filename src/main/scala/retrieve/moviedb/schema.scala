package retrieve.moviedb

import MyPostgresDriver.simple._
import retrieve.freebase.{NamedMovieList, MovieDescriptor}
import play.api.libs.json.{Json, JsValue}
import scalaz.Middle3

/**
 * Created by hassan on 10/03/2014.
 */


object Schema {

  class DBMovieDescriptor(tag: Tag) extends Table[MovieDescriptor](tag, "movie") {

    import retrieve.freebase.MovieToJson.normal._

    def mID = column[String]("m_id", O.PrimaryKey)

    def data = column[JsValue]("data")

    def * = (mID, data).shaped <>(
      (d: (String, JsValue)) => d._2.as[MovieDescriptor],
      (md: MovieDescriptor) => Some((md.mid, Json.toJson(md)))
      )
  }

  val dbMovieDescriptor = TableQuery[DBMovieDescriptor]

  class DBNamedMovieList(tag: Tag) extends Table[NamedMovieList](tag, "named_movie_list") {
    def read = (name_ : String, query_ : String, movies_ : List[String]) => {
      val materializedMovies = getMoviesWithMIDs(movies_)
      NamedMovieList(name_, query_, materializedMovies)
    }

    def write(nml: NamedMovieList) = {
      val movieMIDs = insertMissingMovies(nml.md)
      Some((nml.name, nml.query, movieMIDs.toList))
    }

    def name = column[String]("name")

    def query = column[String]("query")

    def movies = column[List[String]]("movies")

    def pk = primaryKey("pkey", (name, query))

    def * = (name, query, movies).shaped <>(read.tupled, write _)
  }

  val dbNamedMovieList = TableQuery[DBNamedMovieList]

}
