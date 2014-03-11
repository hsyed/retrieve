package retrieve.freebase

import org.scalatest._

abstract class UnitSpec extends FlatSpec with Matchers with
OptionValues with Inside with Inspectors

/**
 * Created by hassan on 11/03/2014.
 */
class MovieQueryTests extends UnitSpec {
  it should "generate the correct json output." in {
    assert(CannesFestival("2012").uri != Some("{}"))
  }
}
