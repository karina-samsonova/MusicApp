package com.example.favorites.domain.model

data class PlaylistResponse(
    val next: String?,
    var content: List<Playlist>
)
