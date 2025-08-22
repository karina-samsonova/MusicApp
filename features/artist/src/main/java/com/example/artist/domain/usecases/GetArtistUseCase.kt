package com.example.artist.domain.usecases

import com.example.artist.domain.ArtistRepository
import javax.inject.Inject

class GetArtistUseCase @Inject constructor(
    private val repository: ArtistRepository
) {
    suspend operator fun invoke(id: String) =
        repository.getArtist(id)
}