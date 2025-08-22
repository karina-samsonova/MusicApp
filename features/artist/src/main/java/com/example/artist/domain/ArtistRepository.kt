package com.example.artist.domain

import com.example.artist.domain.model.AlbumResponse
import com.example.artist.domain.model.Artist
import com.example.artist.domain.model.TrackResponse

interface ArtistRepository {

    suspend fun getArtist(id: String): Artist

    suspend fun getArtistTracks(id: String, limit: Int, offset: Int?): TrackResponse

    suspend fun getArtistAlbums(id: String, limit: Int, offset: Int?): AlbumResponse

    suspend fun isFavorite(id: String): Boolean

    suspend fun favor(id: String)

    suspend fun disfavor(id: String)
}