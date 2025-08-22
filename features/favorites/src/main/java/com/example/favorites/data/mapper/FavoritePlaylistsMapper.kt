package com.example.favorites.data.mapper

import com.example.database.entities.FavoritePlaylist
import javax.inject.Inject

internal class FavoritePlaylistsMapper @Inject constructor() {

    fun mapListEntityToString(entity: List<FavoritePlaylist>): List<String> =
        entity.map { it.id }
}