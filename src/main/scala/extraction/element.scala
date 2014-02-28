package extraction

import retrieve.{TorrentSize, GenericTorrent}
import org.jsoup.nodes.{Document, Element}

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

sealed trait Extractor extends ExtractorBase {
  def extract : extractee
}

class PirateGenericExtractor(e: Element) extends Extractor {
  type extractee  = GenericTorrent
  def exname = "pbay torrent row"

  val row = e.select("td")

  def name = fieldExtractor("name") ( row.get(1).select("a").html )

  def size = fieldExtractor("size") ( row.get(4).html.split("&nbsp;") match {
    case Array(value, tag) => (tag,value.toFloat) match {
      case ("B"   , v )  => TorrentSize(v.toLong)
      case ("KiB" , v )  => TorrentSize((v * TorrentSize.KB).toLong)
      case ("MiB" , v )  => TorrentSize((v * TorrentSize.MB).toLong)
      case ("GiB" , v )  => {
        TorrentSize((v * TorrentSize.GB).toLong)
      }
    }
    case _ => throw new Exception(f"${row.get(4).html} : error in size parsing")
  } )

  def seeders = fieldExtractor("seeders") ( Integer.parseInt(row.get(5).html) )

  def leachers = fieldExtractor("leachers") ( Integer.parseInt(row.get(6).html) )

  def magnetLink = fieldExtractor("magnetLink") ( Some(row.get(3).select("nobr a").attr("href")))

  def extract = GenericTorrent(name, size,seeders,leachers, magnetLink)
}