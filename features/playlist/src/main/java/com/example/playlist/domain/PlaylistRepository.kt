package com.example.playlist.domain

import com.example.playlist.domain.model.Playlist

interface PlaylistRepository {

    suspend fun getPlaylist(id: String): List<Playlist>

    suspend fun isFavorite(id: String): Boolean

    suspend fun favor(id: String)

    suspend fun disfavor(id: String)
}