package com.example.search.domain.usecases

import com.example.search.domain.SearchRepository
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(id: String, namesearch: String, limit: Int, offset: Int?) =
        repository.getAlbums(id, namesearch, limit, offset)
}