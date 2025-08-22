package com.example.search.domain.usecases

import com.example.search.domain.SearchRepository
import com.example.search.domain.model.ContentType
import javax.inject.Inject

class GetAutocompleteUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(prefix: String, type: ContentType) =
        repository.getAutocomplete(prefix, type)
}