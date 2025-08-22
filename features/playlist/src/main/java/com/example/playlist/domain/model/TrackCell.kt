package com.example.playlist.domain.model

data class TrackCell(
    val id: String,
    val position: Int,
    val image: String,
    val name: String,
    val artist_name: String,
    val duration: Int,
    val audio: String,
    val audiodownload: String,
    val audiodownload_allowed: Boolean,
)
