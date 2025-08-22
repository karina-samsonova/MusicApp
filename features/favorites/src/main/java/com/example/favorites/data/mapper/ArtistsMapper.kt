package com.example.favorites.data.mapper

import com.example.favorites.domain.model.Artist
import com.example.favorites.domain.model.ArtistResponse
import com.example.favorites.domain.model.TrackCell
import com.example.network.model.ArtistDto
import com.example.network.model.ArtistResponseDto
import com.example.network.model.ArtistTrackDto
import javax.inject.Inject

internal class ArtistsMapper @Inject constructor() {

    fun mapDtoToEntity(dto: ArtistDto) = Artist(
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
        duration = dto.duration.toIntOrNull() ?: 0,
        audio = dto.audio,
        audiodownload = dto.audiodownload,
        audiodownload_allowed = dto.audiodownload_allowed,
    )

    fun mapListDtoToEntity(dto: List<ArtistTrackDto>, artistName: String): List<TrackCell> =
        dto.map { mapDtoToEntity(it, artistName) }
}