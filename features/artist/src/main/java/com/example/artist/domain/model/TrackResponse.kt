package com.example.artist.domain.model

data class TrackResponse(
    val next: String?,
    val content: List<TrackCell>
)
