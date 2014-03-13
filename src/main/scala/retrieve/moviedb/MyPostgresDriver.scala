package retrieve.moviedb
import slick.driver.PostgresDriver
import com.github.tminglei.slickpg._

/**
 * Created by hassan on 11/03/2014.
 */
trait MyPostgresDriver  extends PostgresDriver
                        with    PgArraySupport
                        with    PgPlayJsonSupport
                        with    PgHStoreSupport {

  override val Implicit = new ImplicitsPlus {}
  override val simple =  new SimpleQLPlus {}

  trait ImplicitsPlus extends Implicits
                      with    ArrayImplicits
                      with    JsonImplicits
                      with    HStoreImplicits

  trait SimpleQLPlus  extends SimpleQL
                      with    ImplicitsPlus
}

object MyPostgresDriver extends MyPostgresDriver
