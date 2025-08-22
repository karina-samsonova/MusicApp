package com.example.search.data.mapper

import com.example.network.model.ArtistDto
import com.example.network.model.ArtistResponseDto
import com.example.network.model.ArtistTrackDto
import com.example.search.domain.model.Artist
import com.example.search.domain.model.ArtistResponse
import com.example.search.domain.model.TrackCell
import javax.inject.Inject

internal class ArtistsMapper @Inject constructor() {

    private fun mapDtoToEntity(dto: ArtistDto) = Artist(
        id = dto.id,
        name = dto.name,
        website = dto.website,
        joindate = dto.joindate,
        image = dto.image,
        shorturl = dto.shorturl,
        shareurl = dto.shareurl,
        tracks = mapListDtoToEntity(dto.tracks ?: emptyList(), dto.name)
    )

    fun mapListDtoToEntity(dto: ArtistResponseDto): ArtistResponse {
        return ArtistResponse(dto.headers.next, dto.results.map { mapDtoToEntity(it) })
    }

    private fun mapDtoToEntity(dto: ArtistTrackDto, artistName: String) = TrackCell(
        id = dto.id,
        position = 0,
        image = dto.image,
        name = dto.name,
        artist_name = artistName,
        duration = dto.duration.toInt(),
        audio = dto.audio,
        audiodownload = dto.audiodownload,
        audiodownload_allowed = dto.audiodownload_allowed,
    )

    private fun mapListDtoToEntity(dto: List<ArtistTrackDto>, artistName: String): List<TrackCell> =
        dto.map { mapDtoToEntity(it, artistName) }
}