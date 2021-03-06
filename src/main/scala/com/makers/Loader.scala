package com.makers

import com.makers.model._

import org.json4s._
import org.json4s.jackson.JsonMethods._


object Loader {
  def load(file: String) = {
    val raw = io.Source.fromFile(file).mkString
    implicit val formats = DefaultFormats
    val users = parse(raw).extract[List[User]]
    users.toSet
  }
}
