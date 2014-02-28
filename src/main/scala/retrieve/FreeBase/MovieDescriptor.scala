package retrieve.FreeBase
import scalaz._
import Scalaz._

/**
 * Created by hassan on 28/02/2014.
 */
case class MovieDescriptor(title : String, initialReleaseDate: String)

trait MovieDescriptorOps {
  val showMovieDescriptor = new Show[MovieDescriptor] {
    override def shows(movd : MovieDescriptor) : String = {
      import movd._
      f"$title , $initialReleaseDate"
    }
  }
}
