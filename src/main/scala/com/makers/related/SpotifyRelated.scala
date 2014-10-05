package com.makers.related

import scala.collection.JavaConverters._

import com.makers.model.Artist
import com.makers.util.SpotifyApi


case class SpotifyRelated(val artist: Artist, val related: List[Artist]) extends RelatedArtists

object SpotifyRelated {

  def apply(api: SpotifyApi, artist: Artist): SpotifyRelated =
    SpotifyRelated(
      artist,
      api.get.getArtistRelatedArtists(artist.id).build.get.asScala.toList.map(Artist(_))
    )

  def apply(api: SpotifyApi, artists: List[Artist]): List[SpotifyRelated] = artists.map(SpotifyRelated(api, _))
}
