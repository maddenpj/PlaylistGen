package com.makers

import com.makers.model._

import breeze.linalg._

import scalaz._
import Scalaz._

class Recommender(val users: Set[User]) {
  type IndexedArtists = List[(String, Int)]
  val uniques: IndexedArtists = uniqueArtists.toList.zipWithIndex

  def recommend(artists: List[Artist]) = {
    val inputV = toSV(indexUniques(artists))
    val universe = users.flatMap(_.playlists).filter(_.uniqueArtists.size > 4)

    val similar = universe.map { pl =>
      val plV = playlistSV(pl)
      (pl, (inputV dot plV) / (inputV.norm() * plV.norm() ))
    }
    similar.toList.sortWith(_._2 > _._2)
  }

  def recommendCount(artists: List[Artist]): List[(Playlist, Double)] = {
    val inputV = toSV(indexUniques(artists))
    val universe = users.flatMap(_.playlists).filter(_.uniqueArtists.size > 4)

    val similar = universe.map { pl =>
      val plV = countSV(countUniques(pl.songs.map(_.artist)))
      (pl, (inputV dot plV) / (inputV.norm() * plV.norm() ))
    }
    similar.toList.sortWith(_._2 > _._2)
  }

  def popularityThreshold(artists: List[Artist], rs: List[(Playlist, Double)], lower: Int = 10, upper: Int = 10) = {
    val base = artists.map(a => medianPopularity(allSongsForArtist(a))).sum / artists.length
    rs.filter { case (pl, score) =>
      within(medianPopularity(pl.songs), base, lower, upper)
    }
  }

  def within(x: Int, base: Int, lower: Int, upper: Int) = (x > base - lower && x < base + upper)

  def allSongsForArtist(artist: Artist): List[Song] =
    users.toList.flatMap(_.playlists)
      .flatMap(_.songs)
      .filter(_.artist == artist)

  def medianPopularity(songs: List[Song]): Int = songs.map(_.popularity).sorted.drop(songs.length/2).head

  // Helpers
  def playlistSV(pl: Playlist) = toSV(indexUniques(pl.songs.map(_.artist).toSet.toList))

  def toSV(is: List[Int], size: Int = uniques.length) = {
    val vec = SparseVector.zeros[Int](size)
    is.map(vec(_) = 1)
    vec
  }

  def countSV(is: List[(Int, Int)], size: Int = uniques.length) = {
    val vec = SparseVector.zeros[Int](size)
    is.map { case (idx, count) => vec(idx) = count }
    vec
  }

  def countUniques(input: List[Artist]): List[(Int, Int)] = {
    val i = input.map(_ -> 1).foldMap(Map(_)).toList.map { case (artist, count) =>
      uniques.find(_._1 == artist.name).map(x => (x._2, count))
    }
    i.flatten
  }


  def indexUniques(input: List[Artist]): List[Int] =
    for {
      a <- input
      (artist, idx) <- uniques if artist == a.name
    } yield(idx)

  def uniqueArtists() = {
    val artists = for {
      user <- users
      playlist <- user.playlists
      song <- playlist.songs
    } yield song.artist.name
    artists
  }
}
