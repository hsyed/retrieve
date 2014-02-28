package retrieve

class TorrentSize(val sz : Long) extends AnyVal {
  import TorrentSize._

  override def toString : String = (f"${sz.toFloat / MB} MB")
}
object TorrentSize {
  val KB = 1024
  val MB = 1024 * 1024
  val GB = 1024 * 1024 * 1024
  def apply(sz : Long) = new TorrentSize(sz)

}
trait TorrentEntity {
  def name : String
  def size : TorrentSize
  def seeders : Int
  def leachers : Int

  override def toString : String = f"name : $name, size : $size, s : $seeders"
}

object NameTools {
  case class MediumTagVariants(tag : String, variants : Array[String])
  case class ResolutionTagVariants(tag: String, variants : Array[String])

  def dvd = MediumTagVariants("dvd", Array("DVDRip"))
  def br = MediumTagVariants("br", Array("BluRay", "BRRip"))

  def p1080 = ResolutionTagVariants("1080p",Array("1080p"))
  def p720 = ResolutionTagVariants("720p", Array("720p"))

  def MediumTag : Option[String] = ???
  def ResolutionTag : Option[String] = ???
}

case class GenericTorrent(name : String, size : TorrentSize, seeders : Int, leachers: Int,  magnetLink : Option[String] = None) extends TorrentEntity