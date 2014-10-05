package com.makers.model

import scala.collection.JavaConverters._

import com.makers.util._

import com.wrapper.spotify.models.{Artist => SArtist, Page, SimpleAlbum, SimpleArtist, SimplePlaylist, Track, User => SUser}
import com.wrapper.spotify.methods.UserPlaylistsRequest

import com.echonest.api.v4.{Artist => ENArtist}

import scalaz._
import Scalaz._

trait SpotifyEntity {
  def id: String
  def href: String
  def uri: String

  override def equals(o: Any) = o match {
    case that: SpotifyEntity => that.id == this.id
    case _ => false
  }
  override def hashCode = id.hashCode
}

case class Playlist(
  val id: String,
  val href: String,
  val uri: String,
  val name: String,
  val description: String,
  val followerCount: Int,
  val owner: UserInfo,
  val songs: List[Song]
) extends SpotifyEntity with Jsonable {

  def songCount() = songs.map(_ -> 1).foldMap(Map(_))

  def artistCount() = songs.map(_.artist -> 1).foldMap(Map(_))

  def uniqueArtists() = songs.map(_.artist.name).toSet

  def pretty() =
  s"""
    |Playlist: $name ($href) owner: ${owner.id}
    |($followerCount followers) $description
    |  ${songs.map(x => x.name + " '- " + x.artist.name).mkString(",\n  ")}
  """.stripMargin
}

case class Song(
  val id: String,
  val href: String,
  val uri: String,
  val name: String,
  val album: Album,
  val artist: Artist,
  val featured: List[Artist],
  val duration: Int,
  val popularity: Int
) extends SpotifyEntity with Jsonable

case class Album(
  val id: String,
  val href: String,
  val uri: String,
  val name: String
) extends SpotifyEntity with Jsonable

case class Artist(
  val id: String,
  val href: String,
  val uri: String,
  val name: String
) extends SpotifyEntity with Jsonable

case class UserInfo(
  val id: String,
  val href: String,
  val uri: String
) extends SpotifyEntity with Jsonable


object Playlist {
  def apply(api: SpotifyApi, user: UserInfo, sp: SimplePlaylist): Playlist = {
    val playlist = api.get.getPlaylist(sp.getOwner.getId, sp.getId).build.get
    val owner = UserInfo(playlist.getOwner)
    val songs = playlist.getTracks.getItems.asScala.map(_.getTrack).map(Song(_))
    //println(playlist.getFollowers.getHref)
    Playlist(
      sp.getId,
      sp.getHref,
      sp.getUri,
      sp.getName,
      playlist.getDescription,
      playlist.getFollowers.getTotal,
      owner,
      songs.toList)
  }
}

object Song {
  def apply(t: Track): Song = {
    val album = Album(t.getAlbum)
    val artists = t.getArtists.asScala
    val artist = Artist(t.getArtists.asScala.head)
    val featured = t.getArtists.asScala.tail.map(Artist(_)) // Seems like real artist is always first
    Song(t.getId,
      t.getHref,
      t.getUri,
      t.getName,
      album,
      artist,
      featured.toList,
      t.getDuration,
      t.getPopularity
    )
  }
}

object Album {
  def apply(a: SimpleAlbum): Album = Album(a.getId, a.getHref, a.getUri, a.getName)
}

object Artist {
  def apply(a: SimpleArtist): Artist = Artist(a.getId, a.getHref, a.getUri, a.getName)
  def apply(a: SArtist): Artist = Artist(a.getId, a.getHref, a.getUri, a.getName)
  def apply(a: ENArtist): Artist = {
    val uri = a.getForeignID("spotify")
    val id = uri.split(":").reverse.head
    Artist(id, s"https://api.spotify.com/v1/artists/$id", uri, a.getName)
  }
}

object UserInfo {
  def apply(api: SpotifyApi, userId: String): UserInfo = {
    val u = api.get.getUser(userId).build.get
    UserInfo(u)
  }
  def apply(su: SUser): UserInfo = UserInfo(su.getId, su.getHref, su.getUri)
}
