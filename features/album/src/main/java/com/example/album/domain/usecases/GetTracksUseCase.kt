package com.example.album.domain.usecases

import com.example.album.domain.AlbumRepository
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(id: String, limit: Int, offset: Int?) =
        repository.getAlbumTracks(id, limit, offset)
}