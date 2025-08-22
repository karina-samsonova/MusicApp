package com.example.search.data.mapper

import com.example.network.model.PlaylistDto
import com.example.network.model.PlaylistResponseDto
import com.example.network.model.PlaylistTrackDto
import com.example.search.domain.model.Playlist
import com.example.search.domain.model.PlaylistResponse
import com.example.search.domain.model.TrackCell
import javax.inject.Inject

internal class PlaylistsMapper @Inject constructor() {

    private fun mapDtoToEntity(dto: PlaylistDto) = Playlist(
        id = dto.id,
        name = dto.name,
        creationdate = dto.creationdate,
        user_id = dto.user_id,
        user_name = dto.user_name,
        zip = dto.zip,
        shorturl = dto.shorturl,
        shareurl = dto.shareurl,
        tracks = mapListDtoToEntity(dto.tracks ?: emptyList())
    )

    fun mapListDtoToEntity(dto: PlaylistResponseDto): PlaylistResponse {
        return PlaylistResponse(dto.headers.next, dto.results.map { mapDtoToEntity(it) })
    }

    private fun mapDtoToEntity(dto: PlaylistTrackDto) = TrackCell(
        id = dto.id,
        name = dto.name,
        duration = dto.duration.toInt(),
        artist_name = dto.artist_name,
        position = dto.position.toInt(),
        image = dto.image,
        audio = dto.audio,
        audiodownload = dto.audiodownload,
        audiodownload_allowed = dto.audiodownload_allowed,
    )

    private fun mapListDtoToEntity(dto: List<PlaylistTrackDto>): List<TrackCell> =
        dto.map { mapDtoToEntity(it) }
}