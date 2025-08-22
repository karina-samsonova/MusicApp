package com.example.album.data.mapper

import com.example.album.domain.model.Album
import com.example.network.model.AlbumDto
import com.example.network.model.AlbumResponseDto
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
        is_favorite = null
    )

    fun mapListDtoToEntity(dto: AlbumResponseDto): Album =
        mapDtoToEntity(dto.results[0])

}