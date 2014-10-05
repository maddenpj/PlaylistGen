package com.makers.util

import com.makers.model._

import com.echonest.api.v4.EchoNestAPI
import com.echonest.api.v4.ArtistParams


class EchoNestApi(apiKey: String) {
  private val api = new EchoNestAPI(apiKey)
  api.setTraceRecvs(true)
  api.setTraceSends(true)

  def artist(artist: Artist, limit: Int = 20) = {
    val ap = new ArtistParams()
    ap.addIDSpace("spotify-WW")
    ap.setName(artist.name)
    ap.setLimit(true)
    ap.setResults(limit)
    ap
  }

  def artists(as: List[Artist], limit: Int = 20) = {
    val ap = new ArtistParams()
    ap.addIDSpace("spotify-WW")
    as.foreach(a => ap.addName(a.name))
    ap.setLimit(true)
    ap.setResults(limit)
    ap
  }

  def get() = api
}
