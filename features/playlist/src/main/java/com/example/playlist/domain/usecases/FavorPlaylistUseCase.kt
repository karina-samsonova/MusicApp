package com.example.playlist.domain.usecases

import com.example.playlist.domain.PlaylistRepository
import javax.inject.Inject

class FavorPlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(id: String) =
        repository.favor(id)
}