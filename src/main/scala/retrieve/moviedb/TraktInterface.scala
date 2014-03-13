package retrieve.moviedb
import MyPostgresDriver.simple._
import retrieve.trakt.TraktMovieSummary

/**
 * Created by hassan on 13/03/2014.
 */
object TraktInterface {
  import SchemaTrakt._

  def get(freebase_id : String) : Option[TraktMovieSummary] =
    db.dbInstance.withSession { implicit session =>
      dbTraktMovieSummary.filter(x=> x.mid === freebase_id).firstOption
    }

  def put(summary : TraktMovieSummary) : Unit =
    db.dbInstance.withSession { implicit session =>
      if( ! dbTraktMovieSummary.filter(x=> x.mid === summary.mid).exists.run)
        dbTraktMovieSummary.insert (summary)
    }
}
