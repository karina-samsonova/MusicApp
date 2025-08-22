package com.example.exoplayer.domain.usecases

import com.example.exoplayer.domain.PlayerRepository
import javax.inject.Inject

class GetFavoriteTracksUseCase @Inject constructor(
    private val repository: PlayerRepository
) {
    suspend operator fun invoke() =
        repository.getFavoriteTracks()
}