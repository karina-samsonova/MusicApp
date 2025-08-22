package com.example.album.domain

import com.example.album.domain.model.Album
import com.example.album.domain.model.TrackResponse

interface AlbumRepository {

    suspend fun getAlbum(id: String): Album

    suspend fun getAlbumTracks(id: String, limit: Int, offset: Int?): TrackResponse

    suspend fun isFavorite(id: String): Boolean

    suspend fun favor(id: String)

    suspend fun disfavor(id: String)

}