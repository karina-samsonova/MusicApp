package com.example.exoplayer.domain.usecases

import com.example.exoplayer.domain.PlayerRepository
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
    private val repository: PlayerRepository
) {
    suspend operator fun invoke(
        id: String,
        artistId: String,
        albumId: String,
        namesearch: String,
        tags: String,
        limit: Int,
        offset: Int?,
        order: String
    ) =
        repository.getTracks(id, artistId, albumId, namesearch, tags, limit, offset, order)
}