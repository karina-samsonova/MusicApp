package com.example.search.data.mapper

import com.example.network.model.TrackDto
import com.example.network.model.TrackResponseDto
import com.example.search.domain.model.TrackCell
import com.example.search.domain.model.TrackResponse
import javax.inject.Inject

internal class TrackCellsMapper @Inject constructor() {

    private fun mapDtoToEntity(dto: TrackDto) = TrackCell(
        id = dto.id,
        name = dto.name,
        duration = dto.duration,
        artist_name = dto.artist_name,
        position = dto.position,
        audio = dto.audio,
        audiodownload = dto.audiodownload,
        image = dto.image,
        audiodownload_allowed = dto.audiodownload_allowed,
    )

    fun mapListDtoToEntity(dto: TrackResponseDto): TrackResponse {
        return TrackResponse(dto.headers.next, dto.results.map { mapDtoToEntity(it) })
    }

}