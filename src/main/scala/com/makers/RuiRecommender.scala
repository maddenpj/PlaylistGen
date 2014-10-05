package com.makers

import com.makers.model._

import breeze.linalg._

class RuiRecommender(val users: Set[User]) {
// loop thru all playlxists and pick out ones that have at least on of the artists in it
// make a hash of the songs with the count
// also make a hash of the artists with the count
    def recommend(artists: List[Artist]) = {
        val hashofrelated = collection.mutable.Map[Song, Int]()
        val universe = users.flatMap(_.playlists).filter(_.uniqueArtists.size > 4)
        val considered = universe.map { playlist => 
            val listofsongs = playlist.songs
            listofsongs.map { song => 
                val artistofsong = song.artist 
                if (artists contains artistofsong) {
                    hashofrelated(song) = hashofrelated.getOrElse(song, 0) 
                }
            }
         }
         println(hashofrelated)
    }
//
//    def recommendartist(artists: List[Artist]) = {
//        val hashofrelatedartists = collection.mutable.Map[List, Int]
//        val universe = users.flatMap(_.playlists).filter(_.uniqueArtists.size > 4)
//        val considered = universe.map {playlist =>
//            val listofartists = playlist.songs.map {song => 
//                song.artist 
//            }
//        }
//    }
}

