package com.example.search.data.mapper

import com.example.network.model.AutocompleteResponseDto
import com.example.network.model.MatchDto
import com.example.search.domain.model.Autocomplete
import com.example.search.domain.model.ContentType
import javax.inject.Inject

internal class AutocompleteMapper @Inject constructor() {

    private fun mapDtoToEntity(dto: MatchDto) = Autocomplete(
        match = dto.match,
        count = dto.count
    )

    fun mapListDtoToEntity(dto: AutocompleteResponseDto, type: ContentType): List<String> {
        val result = when (type) {
            ContentType.ARTISTS -> dto.results.artists?.map { mapDtoToEntity(it) } ?: emptyList()
            ContentType.TRACKS -> dto.results.tracks?.map { mapDtoToEntity(it) } ?: emptyList()
            ContentType.ALBUMS -> dto.results.albums?.map { mapDtoToEntity(it) } ?: emptyList()
            ContentType.PLAYLISTS -> dto.results.tags?.map { mapDtoToEntity(it) } ?: emptyList()
        }
        return result.sortedByDescending { it.count }.map { it.match }.distinct()
    }
}