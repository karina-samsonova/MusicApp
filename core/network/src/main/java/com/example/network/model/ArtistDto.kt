package com.example.network.model

data class ArtistDto(
    val id: String,
    val name: String,
    val website: String,
    val joindate: String,
    val image: String,
    val shorturl: String,
    val shareurl: String,
    val tracks: List<ArtistTrackDto>? = emptyList(),
    val albums: List<ArtistAlbumDto>? = emptyList()
)
