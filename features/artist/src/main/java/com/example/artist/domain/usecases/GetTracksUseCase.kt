package com.example.artist.domain.usecases

import com.example.artist.domain.ArtistRepository
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
    private val repository: ArtistRepository
) {
    suspend operator fun invoke(id: String, limit: Int, offset: Int?) =
        repository.getArtistTracks(id, limit, offset)
}