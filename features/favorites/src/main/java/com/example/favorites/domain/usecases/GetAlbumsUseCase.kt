package com.example.favorites.domain.usecases

import com.example.favorites.domain.FavoritesRepository
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(id: String, namesearch: String, limit: Int, offset: Int?) =
        repository.getAlbums(id = id, namesearch = namesearch, limit = limit, offset = offset)
}