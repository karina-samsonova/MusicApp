package com.example.exoplayer.data.mapper

import com.example.exoplayer.domain.model.TrackCell
import com.example.network.model.TrackDto
import com.example.network.model.TrackResponseDto
import javax.inject.Inject

class TrackCellsMapper @Inject constructor() {

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

    fun mapListDtoToEntity(dto: TrackResponseDto): List<TrackCell> {
        return dto.results.map { mapDtoToEntity(it) }
    }

}