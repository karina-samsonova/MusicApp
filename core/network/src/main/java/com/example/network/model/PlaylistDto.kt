package com.example.network.model

data class PlaylistDto(
    val id: String,
    val name: String,
    val creationdate: String,
    val user_id: String,
    val user_name: String,
    val zip: String,
    val shorturl: String,
    val shareurl: String,
    val tracks: List<PlaylistTrackDto>? = emptyList()
)
