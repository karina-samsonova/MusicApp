package com.example.network.model

data class ArtistTrackDto(
    val album_id: String,
    val album_name: String,
    val id: String,
    val name: String,
    val duration: String,
    val releasedate: String,
    val license_ccurl: String,
    val album_image: String,
    val image: String,
    val audio: String,
    val audiodownload: String,
    val audiodownload_allowed: Boolean,
)
