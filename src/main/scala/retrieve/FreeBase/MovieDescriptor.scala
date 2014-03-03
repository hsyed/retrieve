package retrieve.freebase
import scalaz._
import Scalaz._

/**
 * Created by hassan on 28/02/2014.
 */
case class MovieDescriptor(title : String, initialReleaseDate: String, genres: List[String], directedBy : List[String], subjects : List[String],trailers : List[String])
case class MovieDescriptors(md : List[MovieDescriptor])

trait MovieDescriptorOps {
  implicit val showMovieDescriptor = new Show[MovieDescriptor] {
    override def shows(movd : MovieDescriptor) : String = { import movd._
      f"$initialReleaseDate%-12s  $title"
    }
  }

  implicit val showMovieDescriptors = new Show[MovieDescriptors] {
    override def shows(movd : MovieDescriptors) : String = {
      movd.md.map(_.show).mkString("\n")
    }
  }
}
