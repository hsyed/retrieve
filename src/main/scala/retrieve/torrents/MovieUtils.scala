package retrieve.torrents

import retrieve.freebase.{MovieDescriptor, MovieDescriptors}
import scala.concurrent.Future
import retrieve.scalay._

/**
 * Created by hassan on 07/03/2014.
 */
case class MovieTorrent(movie: MovieDescriptor, torrent: List[GenericTorrent])
case class MovieTorrentSearchResponse(results : List[MovieTorrent]) {
  private val pWithoutResults = (_:MovieTorrent).torrent.isEmpty
  private val pWithLowSeedCount = (_:MovieTorrent).torrent.headOption.map(_.seeders<10).getOrElse(true)
  private val pDeficientTorrents = pWithoutResults || pWithLowSeedCount

  def withResults = results.filter(z=>z.torrent.nonEmpty)
  def withoutResults = results.filter(pWithoutResults)
  def withLowSeedCount = results.filter(pWithLowSeedCount)
  def consideredDeficient = results.filter(pDeficientTorrents)
  def nonDeficient = results.filter(!pDeficientTorrents)
}

trait MovieUtils {
  implicit class pimpMusicDescriptors(movieList: MovieDescriptors) {
    def dispatchSearch = Future.sequence(movieList.md.map(x =>
      PirateSource.extractBlanketQueryFuture(x.imdb_id.head)
        .map(y => MovieTorrent(x, y))
    ))

    def pirateSearch = dispatchSearch.map(x=> {

      MovieTorrentSearchResponse(x)
    })
  }

}
