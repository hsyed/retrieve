package retrieve.freebase

import scala.slick.driver.HsqldbDriver
/**
 * Created by hassan on 02/03/2014.
 */


package object db extends HsqldbDriver.SimpleQL with HsqldbDriver.Implicits{

}
