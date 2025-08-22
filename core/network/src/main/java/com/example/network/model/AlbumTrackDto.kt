package com.example.network.model

data class AlbumTrackDto(
    val id: String,
    val position: String,
    val name: String,
    val duration: String,
    val license_ccurl: String,
    val audio: String,
    val audiodownload: String,
    val audiodownload_allowed: Boolean,
)
