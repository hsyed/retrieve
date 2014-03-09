package extraction

import org.jsoup.nodes.{Document, Element}
import retrieve.torrents.{TorrentSize, GenericTorrent}

/**
 * Created by hassan on 15/02/2014.
 */

sealed trait ExtractorBase {
  type extractee
  def exname : String

  def fieldExtractor[T](name: String)(f: => T ) : T = {
    val t : T = try f
      catch {
        case e: Exception =>
          println(f"Field Extractor Exception $exname - $name")
          throw e
      }
    t
  }

  // TODO figure out which exceptions are actually thrown
  def fieldExtractorOption[T](name : String)(f: => T) : Option[T] = {
    val t : Option[T] = try Some(f)
    catch {
      case e: Exception =>
        None
    }
    t
  }
}

trait Extractor extends ExtractorBase {
  def extract : extractee
}

