package com.example.search.data.mapper

import com.example.network.model.AlbumDto
import com.example.network.model.AlbumResponseDto
import com.example.network.model.AlbumTrackDto
import com.example.search.domain.model.Album
import com.example.search.domain.model.AlbumResponse
import com.example.search.domain.model.TrackCell
import javax.inject.Inject

internal class AlbumsMapper @Inject constructor() {

    private fun mapDtoToEntity(dto: AlbumDto) = Album(
        id = dto.id,
        name = dto.name,
        releasedate = dto.releasedate,
        artist_id = dto.artist_id,
        artist_name = dto.artist_name,
        image = dto.image,
        zip = dto.zip,
        shorturl = dto.shorturl,
        shareurl = dto.shareurl,
        zip_allowed = dto.zip_allowed,
        tracks = mapListDtoToEntity(dto.tracks ?: emptyList(), dto.image, dto.artist_name)
    )

    fun mapListDtoToEntity(dto: AlbumResponseDto): AlbumResponse {
        return AlbumResponse(dto.headers.next, dto.results.map { mapDtoToEntity(it) })
    }

    private fun mapDtoToEntity(dto: AlbumTrackDto, image: String, artistName: String) = TrackCell(
        id = dto.id,
        position = dto.position.toInt(),
        image = image,
        name = dto.name,
        artist_name = artistName,
        duration = dto.duration.toInt(),
        audio = dto.audio,
        audiodownload = dto.audiodownload,
        audiodownload_allowed = dto.audiodownload_allowed,
    )

    private fun mapListDtoToEntity(dto: List<AlbumTrackDto>, image: String, artistName: String): List<TrackCell> =
        dto.map { mapDtoToEntity(it, image, artistName) }
}