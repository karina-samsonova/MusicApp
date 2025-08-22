package com.example.album.domain.usecases

import com.example.album.domain.AlbumRepository
import javax.inject.Inject

class DisfavorAlbumUseCase @Inject constructor(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(id: String) =
        repository.disfavor(id)
}