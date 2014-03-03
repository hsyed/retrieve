package retrieve.freebase.db

/**
 * Created by hassan on 02/03/2014.
 */
object Atoms {
  case class Festival(mid: String, name : String)

  class Festivals(tag: Tag) extends Table[Festival](tag,"festivals") {
    def mid = column[String]("mid",O.PrimaryKey)
    def name = column[String]("name", O.NotNull)

    def * =  (mid,name) <> (Festival.tupled,Festival.unapply)

    def idx = index("n_idx",name, unique = true)
  }

  case class Subject(mid: String, name : String)

  class Subjects(tag: Tag) extends Table[Subject](tag,"Subjects") {
    def mid = column[String]("mid",O.PrimaryKey)
    def name = column[String]("name", O.NotNull)

    def * =  (mid,name) <> (Subject.tupled,Subject.unapply)

    def idx = index("n_idx",name, unique = true)
  }

  case class Genre(mid: String, name : String)

  class Genres(tag: Tag) extends Table[Genre](tag,"Genres") {
    def mid = column[String]("mid",O.PrimaryKey)
    def name = column[String]("name", O.NotNull)

    def * =  (mid,name) <> (Genre.tupled,Genre.unapply)

    def idx = index("n_idx",name, unique = true)
  }
}
