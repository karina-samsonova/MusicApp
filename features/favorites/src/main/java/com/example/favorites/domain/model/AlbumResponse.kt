package com.example.favorites.domain.model

data class AlbumResponse(
    val next: String?,
    var content: List<Album>
)
