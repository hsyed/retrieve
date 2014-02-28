package retrieve.FreeBase

import spray.json._
import scalaz._
import Scalaz._

/**
 * Created by hassan on 27/02/2014.
 */

//{
//"name": "the walking dead",
//"type": "/tv/tv_program",
//"number_of_episodes": null,
//"number_of_seasons": null,
//"seasons": [{
//"season_number": null,
//"episodes": [{
//"name": null,
//"air_date": null
//}]
//}]
//}
trait MyTVShowQueryProtocol extends DefaultJsonProtocol  {
  implicit object TVShowJsonQueryFormat extends RootJsonFormat[TVShowQuery] {
    def write(q: TVShowQuery) = JsObject(
      "name" -> JsString(q.name),
      "type" -> JsString("/tv/tv_program"),
      "number_of_episodes" -> JsNull,
      "number_of_seasons" -> JsNull,
      "seasons" -> JsArray(JsObject(
        "season_number" -> JsNull,
        "episodes" -> JsArray(JsObject(
          "name" -> JsNull,
          "air_date" -> JsNull
        ))
      ))
    )

    def read(value: JsValue) = ???
  }

  implicit object TVShowJsonResponseFormat extends RootJsonFormat[TVShowDescriptor] {
    def write(q: TVShowDescriptor) = ???

    def read(value: JsValue): TVShowDescriptor = {
      val topLevel = value.asJsObject.fields("result").asJsObject
      topLevel.getFields("name", "number_of_episodes", "number_of_seasons", "seasons") match {
        case Seq(JsString(name), JsNumber(totalEpisodes), JsNumber(totalSeasons), JsArray(seasonsRaw)) =>
          val seasons = seasonsRaw map (_.asJsObject.getFields("season_number", "episodes") match {
            case Seq(JsNumber(seasonNumber), JsArray(episodesRaw)) =>
              val episodes = episodesRaw.view.map(_.asJsObject.fields).map {
                x =>
                  TVShowEpisodeDescriptor(x("name").convertTo[String], x("air_date") match {
                    case JsNull => None
                    case JsString(ad) => Some(ad)
                    case _ => throw new Exception("tv show schema mallformed.")
                  })
              }.toList
              TVShowSeasonDescriptor(seasonNumber.toInt, episodes)
          })
          TVShowDescriptor(name, totalEpisodes.toInt, totalSeasons.toInt, seasons.sortBy(_.number))
      }
    }
  }
}


case class TVShowQuery(name: String)

case class TVShowEpisodeDescriptor(name: String, airDate: Option[String])

case class TVShowSeasonDescriptor(number: Int, episodes: List[TVShowEpisodeDescriptor])

case class TVShowDescriptor(name: String, totalEpisodes: Int, totalSeasons: Int, seasons: List[TVShowSeasonDescriptor])

trait TVShowOps {
  implicit val tvshowepisodedescriptor = new Show[TVShowEpisodeDescriptor] {
    override def shows(tsd : TVShowEpisodeDescriptor) : String = {
      import tsd._
      val airdate = airDate.getOrElse("Unknown")
      f"$airdate%-12s $name"
    }
  }
  implicit val tvshowseasondescriptorshow = new Show[TVShowSeasonDescriptor] {
    override def shows(tsd : TVShowSeasonDescriptor) : String = {
      import tsd._
      f"""Season $number Episodes : ${episodes.size}""" +
      "\n----------------------------\n" +
      f"""${episodes.map(_.show).mkString("\n")}"""

    }
  }
  implicit val tvshowdescriptorshow = new Show[TVShowDescriptor] {
    override def shows(tsd : TVShowDescriptor) = {
      import tsd._
      f"""Name : $name
        |Total Episodes : $totalEpisodes
        |Total Seasons : $totalSeasons
        |
        |Seasons :
        |-----------------------------------------------
        |
        |${seasons.view.sortBy(_.number).map(_.show).mkString("\n\n")}
      """.stripMargin
    }
  }
}


