package com.example.favorites.domain.usecases

import com.example.favorites.domain.FavoritesRepository
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(id: String, namesearch: String, limit: Int, offset: Int?) =
        repository.getPlaylists(id = id, namesearch = namesearch, limit = limit, offset = offset)
}