package retrieve.moviedb

import retrieve.freebase.{NamedMovieList, MovieDescriptor}
import MyPostgresDriver.simple._

/**
 * Created by hassan on 11/03/2014.
 */
trait FreebaseInterface {

  import SchemaFreebase._

  def insertMissingMovies(movies: List[MovieDescriptor]) = {
    val movieMIDs = movies.map(_.mid).toSet
    db.dbInstance.withTransaction {
      implicit session =>
        val existingMIDs = dbMovieDescriptor.filter(_.mID inSet movieMIDs).map(_.mID).list
        val nonExistingMIDs = movieMIDs -- existingMIDs

        movies.filter(x => nonExistingMIDs.contains(x.mid)).foreach {
          x =>
            dbMovieDescriptor.insertAll(x)
        }
        movieMIDs
    }
  }

  def getMoviesWithMIDs(mids: List[String]) =
    db.dbInstance.withSession {
      implicit session =>
        dbMovieDescriptor.filter(x => x.mID inSet mids)
          .list
    }


  def saveNamedMovieList(list: NamedMovieList) = db.dbInstance.withSession {
    implicit session => dbNamedMovieList.insert(list)
  }

  def getNamedMovieList(name: String, query: String): Option[NamedMovieList] =
    db.dbInstance.withSession {
      implicit sessions =>
        dbNamedMovieList.filter(x => x.name === name && x.query === query).firstOption
    }
}
