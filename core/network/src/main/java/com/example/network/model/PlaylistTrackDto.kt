package com.example.network.model

data class PlaylistTrackDto(
    val id: String,
    val name: String,
    val album_id: String,
    val artist_id: String,
    val duration: String,
    val artist_name: String,
    val playlistadddate: String,
    val position: String,
    val license_ccurl: String,
    val album_image: String,
    val image: String,
    val audio: String,
    val audiodownload: String,
    val audiodownload_allowed: Boolean,
)
