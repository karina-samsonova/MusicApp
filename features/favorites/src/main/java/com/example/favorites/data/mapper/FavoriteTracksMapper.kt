package com.example.favorites.data.mapper

import com.example.database.entities.FavoriteTrack
import javax.inject.Inject

internal class FavoriteTracksMapper @Inject constructor() {

    fun mapListEntityToString(entity: List<FavoriteTrack>): List<String> =
        entity.map { it.id }
}