package com.example.favorites.domain.model

data class TrackResponse(
    val next: String?,
    var content: List<TrackCell>
)
