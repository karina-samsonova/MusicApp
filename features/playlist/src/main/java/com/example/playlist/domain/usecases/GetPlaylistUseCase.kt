package com.example.playlist.domain.usecases

import com.example.playlist.domain.PlaylistRepository
import javax.inject.Inject

class GetPlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(id: String) =
        repository.getPlaylist(id)
}