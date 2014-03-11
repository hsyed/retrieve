package retrieve.freebase
import retrieve.moviedb
import scalaz._
import Scalaz._

/**
 * Created by hassan on 28/02/2014.
 */
case class MovieDescriptor( mid: String, title : String, initialReleaseDate: String, genres: List[String], directedBy : List[String],
                            subjects : List[String],trailers : List[String], award : List[String],
                            imdb_id : List[String])

case class MovieList(md : List[MovieDescriptor])
case class NamedMovieList(name: String, query: String, md : List[MovieDescriptor])

trait MovieDescriptorOps {
  implicit class NamedMovieListOps(value : NamedMovieList) {
    def saveDB = moviedb.saveNamedMovieList(value)
  }

  implicit val showMovieDescriptor = new Show[MovieDescriptor] {
    override def shows(movd : MovieDescriptor) : String = { import movd._
      f"$initialReleaseDate%-12s  $title"
    }
  }

  implicit val showMovieDescriptors = new Show[NamedMovieList] {
    override def shows(movd : NamedMovieList) : String = {
      f"${movd.md.size} results : \n" +
      movd.md.map(_.show).mkString("\n")
    }
  }
}
