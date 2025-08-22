package com.example.favorites.data.mapper

import com.example.database.entities.FavoriteAlbum
import javax.inject.Inject

internal class FavoriteAlbumsMapper @Inject constructor() {

    fun mapListEntityToString(entity: List<FavoriteAlbum>): List<String> =
        entity.map { it.id }
}