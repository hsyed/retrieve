package retrieve.freebase

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created by hassan on 08/03/2014.
 */
trait MovieToJsonFromFreebase {
   implicit val reader : Reads[MovieDescriptor] = (
      (__ \ "name").read[String] and
      (__ \ "initial_release_date").readNullable[String].map(_.getOrElse("")) and
      (__ \ "genre").read[List[String]] and
      (__ \ "directed_by").read[List[String]] and
      (__ \ "subjects").read[List[String]] and
      (__ \ "trailers").read[List[String]] and
      (__ \ "/award/award_winning_work/awards_won").readNullable[List[JsValue]].map(
        _.getOrElse(Nil).map(x=> (x \ "award" \ "name").as[String])) and
      (__ \ "/imdb/topic/title_id").read[List[String]]
      ).apply(MovieDescriptor)

  implicit object MovieDescriptorsFromFreeBase extends Reads[MovieDescriptors] {
    override def reads(js: JsValue) = new JsSuccess(
        MovieDescriptors(
          (js \ "result").as[List[JsValue]].map {
            x =>
              x.as[MovieDescriptor]
          }
        )
      )
  }
}

