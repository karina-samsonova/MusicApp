package com.example.artist.data.mapper

import com.example.artist.domain.model.Artist
import com.example.network.model.ArtistDto
import com.example.network.model.ArtistResponseDto
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
        is_favorite = null
    )

    fun mapListDtoToEntity(dto: ArtistResponseDto): Artist =
        mapDtoToEntity(dto.results[0])

}