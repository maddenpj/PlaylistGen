package com.makers.related

import scala.collection.JavaConverters._

import com.makers.model.Artist
import com.makers.util.EchoNestApi


case class EchoNestRelated(val artist: Artist, val related: List[Artist]) extends RelatedArtists
case class EchoNestMultiRelated(val artists: List[Artist], val related: List[Artist])

object EchoNestRelated {
  // Don't use this one because it requests can be batched
  def apply(api: EchoNestApi, artist: Artist): EchoNestRelated = {
    val related = api.get.getSimilarArtists(api.artist(artist)).asScala.toList.map(Artist(_))
    EchoNestRelated(artist, related)
  }

  def apply(api: EchoNestApi, artists: List[Artist]): EchoNestMultiRelated = {
    val related = api.get.getSimilarArtists(api.artists(artists)).asScala.toList
      .map { a =>
        try {
          Some(Artist(a))
        } catch {
          case _: Throwable => None
        }
      }
    EchoNestMultiRelated(artists, related.flatten)
  }
}
