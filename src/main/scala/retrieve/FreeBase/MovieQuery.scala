package retrieve.freebase

import play.api.libs.json._

import QueryDSL._

// TODO ADD DEFAULT DATE INJECTION MECHANISM, Cannes films are often missing initial_release_date
//

/**
 * Created by hassan on 28/02/2014.
 */
object MyMovieQueryProtocol {

  private object QueryToJsonInternal {
    private val isDatePredicate = (x: QueryTag) => x.isInstanceOf[BetweenDates]
    private val isExclusionAwardPredicate = (x: QueryTag) => x.isInstanceOf[ExcludeAwardWithName]
    private val isExclusionGenrePredicate = (x: QueryTag) => x.isInstanceOf[ExcludeGenreWithName]
    private val hasSingleYearPredicate = (x: MovieQuery) => {
      val dates = x.predicates.filter(isDatePredicate)
      if (dates.size != 1)
        false
      else !dates.exists {
        x =>
          if (x.name.size > 2)
            true
          else x.name.exists {
            z =>
              """\d\d\d\d""".r.findFirstIn(z.trim) match {
                case Some(_) => false
                case _ => true
              }
          }
      }
    }

    def addOptionalAward(input: Map[String, JsValue]): Map[String, JsValue] = input ++ Map(
      "/award/award_winning_work/awards_won" -> Json.arr(Json.obj(
        "optional" -> JsString("optional"),
        "award" -> Json.obj(
          "name" -> JsNull
        )
      ))
    )


    private val hasSingleSpecifierValue = (_: MovieQuery).specifier.name.size == 1

    trait Processor {
      protected def mkWithShape(shape: Map[String, JsValue])(values: (String, JsValue)*) =
        JsArray(Seq(JsObject(
          (shape ++ values.toMap).toSeq
        )))

      protected def mkDatePredicate(q: MovieQuery): List[(String, JsValue)] = q.predicates.filter(isDatePredicate).head match {
        case BetweenDates(x :: Nil) => List("initial_release_date" -> JsString(x))
        case BetweenDates(x :: y :: Nil) =>
          val l = "initial_release_date>"
          val r = "initial_release_date<"
          if (Integer.parseInt(x) < Integer.parseInt(y)) List(l -> JsString(x), r -> JsString(y))
          else List(r -> JsString(x), l -> JsString(y))
      }

      def validator(q: MovieQuery): Unit

      def toJson(q: MovieQuery): JsValue

      final def apply(q: MovieQuery): JsValue = {
        validator(q);
        toJson(q)
      }
    }

    private val default_shape = Map(
      "mid" -> JsNull,
      "name" -> JsNull,
      "type" -> JsString("/film/film"),
      "initial_release_date" -> JsNull,
      "genre" -> JsArray(),
      "directed_by" -> JsArray(),
      "subjects" -> JsArray(),
      "trailers" -> JsArray(),
      "limit" -> JsNumber(500),
      "/imdb/topic/title_id" -> JsArray()
    )

    val processTitle = new Processor {
      override def validator(x: MovieQuery) =
        if (!hasSingleSpecifierValue(x)) throw new Exception("Multiple Titles not allowed.")

      override def toJson(x: MovieQuery) = mkWithShape(default_shape)(
        "name" -> JsString(x.specifier.name.head)
      )
    }

    val processAward = new Processor {
      override def validator(x: MovieQuery) = {
        if (!hasSingleYearPredicate(x)) throw new Exception("Awards as a specifier should contain a single date predicate, and this predicate should be a year or a year range.")
        if (x.specifier.name.size != 1) throw new Exception("Awards should be present with a single parameter.")
      }

      override def toJson(x: MovieQuery): JsValue = {
        val award_part: Map[String, JsValue] = {
          val exclusionList = x.predicates.filter(isExclusionAwardPredicate).flatMap(_.name.map(JsString(_)))
          val exclussions =
            if (exclusionList.nonEmpty) Map(
              "exclude:award" -> Json.obj(
                "optional" -> JsString("forbidden"),
                "name|=" -> exclusionList
              ))
            else Map[String, JsValue]()

          val award_part_inside = Map("award" -> Json.obj(
            "name" -> JsNull,
            "category_of" -> JsString(x.specifier.name.head)
          ),
            "year" -> JsString(x.predicates.filter(isDatePredicate).head.name.head)
          )
          award_part_inside ++ exclussions
        }

        mkWithShape(default_shape)(
          "x:type" -> JsString("/award/award_winning_work"),
          "/award/award_winning_work/awards_won" -> Json.arr(Json.toJson(award_part))
        )
      }
    }

    val processFestival = new Processor {
      override def validator(x: MovieQuery) = {
        if (!hasSingleYearPredicate(x)) throw new Exception("Film Festivals as a specifier should contain a single date predicate, and this predicate should be a year or a year range.")
        if (x.specifier.name.size != 1) throw new Exception("Only a Single Film Festival can be queried at a time.")
      }

      override def toJson(x: MovieQuery) = {
        val new_shape = {
          val exclussions = x.predicates.filter(isExclusionGenrePredicate).flatMap(_.name.map(JsString(_)))

          if (exclussions.nonEmpty) default_shape ++ Map(
            "exclude:genre" -> Json.arr(Json.obj(
              "optional" -> JsString("forbidden"),
              "name|=" -> exclussions
            ))
          )
          else default_shape
        }

        val datelow = x.predicates.filter(isDatePredicate).head.name.head
        val datehigh = (datelow.toInt + 1).toString
        mkWithShape(addOptionalAward(new_shape))(
          "film_festivals" -> Json.arr(Json.obj(
            "festival" -> JsString(x.specifier.name.head),
            "opening_date>" -> JsString(datelow),
            "opening_date<=" -> JsString(datehigh)
          ))
        )
      }
    }
  }

  trait QueryToJson {
    import QueryToJsonInternal._

    implicit object queryWriter extends Writes[MovieQuery] {
      override def writes(q: MovieQuery) = {
        q.specifier match {
          case Title(_) => processTitle(q)
          case Awards(_) => processAward(q)
          case Festival(_) => processFestival(q)
          case _ => throw new Exception("senseless query")
        }
      }
    }
  }

}


