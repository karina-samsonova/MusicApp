package com.example.search.domain.model

data class TrackResponse(
    val next: String?,
    val content: List<TrackCell>
)
