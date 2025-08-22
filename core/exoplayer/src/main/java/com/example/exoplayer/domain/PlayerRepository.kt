package com.example.exoplayer.domain

import com.example.exoplayer.domain.model.TrackCell

interface PlayerRepository {

    suspend fun getTracks(
        id: String,
        artistId: String,
        albumId: String,
        namesearch: String,
        tags: String,
        limit: Int,
        offset: Int?,
        order: String
    ): List<TrackCell>

    suspend fun getFavoriteTracks(): List<String>

    suspend fun isFavorite(id: String): Boolean

    suspend fun favor(id: String)

    suspend fun disfavor(id: String)

}