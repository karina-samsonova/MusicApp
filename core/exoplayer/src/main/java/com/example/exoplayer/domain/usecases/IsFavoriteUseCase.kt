package com.example.exoplayer.domain.usecases

import com.example.exoplayer.domain.PlayerRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: PlayerRepository
) {
    suspend operator fun invoke(id: String) =
        repository.isFavorite(id)
}