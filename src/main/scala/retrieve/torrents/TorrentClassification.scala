package retrieve.torrents

/**
 * Created by hassan on 07/03/2014.
 */

case class TorrentSizeRange(low : Long, preffered: Long ,high : Long)
object TorrentClassification {
  import TorrentSize ._
  val runtimebase = 90
  val runtimeinterval = 30
  val runtimeIntervalAllowance = 25

  val smallCutoff = 500 * MB
  val ridiculousCutoff = 3 * GB

  val prefferSeeds = 50

  def medium = TorrentSizeRange(650 * MB , 700 * MB, 1200 * MB)
  def high = TorrentSizeRange(1250 * MB, 1800 * MB, 2500 * MB)

  val mediumSize = "medium"
  val highSize = "high"
  val ridiculousSize = "ridiculous"
}
