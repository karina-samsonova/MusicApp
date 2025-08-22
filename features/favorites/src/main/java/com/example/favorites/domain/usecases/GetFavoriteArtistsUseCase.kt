package com.example.favorites.domain.usecases

import com.example.favorites.domain.FavoritesRepository
import javax.inject.Inject

class GetFavoriteArtistsUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke() =
        repository.getFavoriteArtists()
}