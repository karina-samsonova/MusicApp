package com.example.home.domain.usecases

import com.example.home.domain.HomeRepository
import javax.inject.Inject

class GetNewTracksUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke() =
        repository.getNewTracks()
}