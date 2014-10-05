package com.makers.related

import scala.collection.JavaConverters._

import com.makers.model.Artist

trait RelatedArtists {
  def artist: Artist
  def related: List[Artist] // Ranked
}
