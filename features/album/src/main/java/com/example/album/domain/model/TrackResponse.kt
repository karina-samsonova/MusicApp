package com.example.album.domain.model

data class TrackResponse(
    val next: String?,
    val content: List<TrackCell>
)
