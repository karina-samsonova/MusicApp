package com.example.favorites.data.mapper

import com.example.database.entities.FavoriteArtist
import javax.inject.Inject

internal class FavoriteArtistsMapper @Inject constructor() {

    fun mapListEntityToString(entity: List<FavoriteArtist>): List<String> =
        entity.map { it.id }
}