package com.example.exoplayer.data.mapper

import com.example.database.entities.FavoriteTrack
import javax.inject.Inject

class FavoriteTracksMapper @Inject constructor() {

    fun mapListEntityToString(entity: List<FavoriteTrack>): List<String> =
        entity.map { it.id }
}