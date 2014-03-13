package retrieve.moviedb
import MyPostgresDriver.simple._

/**
 * Created by hassan on 10/03/2014.
 */
import com.jolbox.bonecp._

object db {
  val ds = new BoneCPDataSource()
  ds.setJdbcUrl("jdbc:postgresql://localhost/retrieve?characterEncoding=utf-8");		// set the JDBC url
//  ds.setUsername("root");				// set the username
 // ds.setPassword("root");
  ds.setMinConnectionsPerPartition(2);
  ds.setMaxConnectionsPerPartition(30);
  ds.setPartitionCount(3);

  val dbInstance = Database.forDataSource(ds)

  def createSchema = {
    val ddl = SchemaFreebase.dbMovieDescriptor.ddl ++
              SchemaFreebase.dbNamedMovieList.ddl ++
              SchemaTrakt.dbTraktMovieSummary.ddl
    dbInstance.withSession { implicit session =>
      ddl.create
    }
  }
}
