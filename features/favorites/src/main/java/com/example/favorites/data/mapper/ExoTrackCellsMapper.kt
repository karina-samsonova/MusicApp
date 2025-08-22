package com.example.favorites.data.mapper

import com.example.favorites.domain.model.TrackCell
import javax.inject.Inject
import com.example.exoplayer.domain.model.TrackCell as ExoTrackCell

internal class ExoTrackCellsMapper @Inject constructor() {

    private fun mapDtoToEntity(cell: TrackCell) = ExoTrackCell(
        id = cell.id,
        name = cell.name,
        duration = cell.duration,
        artist_name = cell.artist_name,
        position = cell.position,
        audio = cell.audio,
        audiodownload = cell.audiodownload,
        image = cell.image,
        audiodownload_allowed = cell.audiodownload_allowed,
    )

    fun mapListDtoToEntity(list: List<TrackCell>): List<ExoTrackCell> {
        return list.map { mapDtoToEntity(it) }
    }

}