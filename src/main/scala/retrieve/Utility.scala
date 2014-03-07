package retrieve

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import spray.json.{JsonReader, JsValue}

/**
 * Created by hassan on 14/02/2014.
 */
trait Utility {
  implicit class await[T](f : Future[T]) {
    def await = Await.result(f,20.seconds)
  }


  case class JsonExtractionException(field : String, cause : Throwable)
    extends Exception(field,cause)

  case class JsExtractedField(key : String, js: JsValue) {
    def as[T : JsonReader] : T = {
      try { js.convertTo[T] } catch {
        case e : Exception => throw JsonExtractionException(key,e.getCause)
      }
    }
    def apply(keyInner : String) = try {
      JsExtractedField(f"$key - $keyInner".intern(), js.asJsObject.fields(keyInner))
    }
    catch {
      case e : Exception => throw JsonExtractionException(key, e.getCause)
    }
  }

  implicit class sprayJsonMethods(js : JsValue) {
    def apply(key : String) = try {
      JsExtractedField(key,js.asJsObject.fields(key))
    }
    catch {
      case e: Exception => throw JsonExtractionException(key,e.getCause)
    }
  }
}



