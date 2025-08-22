package com.example.favorites.domain.model

data class ArtistResponse(
    val next: String?,
    var content: List<Artist>
)
