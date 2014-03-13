package retrieve.moviedb
import MyPostgresDriver.simple._
import retrieve.trakt.TraktMovieSummary
import retrieve.moviedb.SchemaFreebase.DBNamedMovieList

/**
 * Created by hassan on 13/03/2014.
 */
object SchemaTrakt {
  class DBTraktMovieSummary(tag : Tag) extends Table[TraktMovieSummary](tag, "trakt_movie_summary") {
    def mid =             column[String]("m_id", O.PrimaryKey)
    def title =           column[String]("title")
    def year =            column[Int]("year")
    def released =        column[Int]("released")
    def url =             column[String]("url")
    def trailer =         column[String]("trailer")
    def runtime =         column[Int]("runtime")
    def tagline =         column[String]("tagline", O.DBType("TEXT"))
    def overview =        column[String]("overview", O.DBType("TEXT"))
    def certification =   column[String]("certification")
    def imdb_id =         column[String]("imdb_id")
    def tmdb_id =         column[Int]("tmdb_id")
    def rt_id =           column[Int]("rt_id")
    def last_updated =    column[Int]("last_updated")
    def images =          column[Map[String,String]]("images")
    def genres =          column[List[String]]("genres")
    def ratings =         column[Map[String,String]]("ratings")

    def * = ( mid.?,title,year,released,url,trailer,runtime,tagline,
              overview,certification,imdb_id,tmdb_id,rt_id,last_updated,
              images,genres,ratings).shaped <> (TraktMovieSummary.tupled,TraktMovieSummary.unapply)
  }
  val dbTraktMovieSummary = TableQuery[DBTraktMovieSummary]
}
