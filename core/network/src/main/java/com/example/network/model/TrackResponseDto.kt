package com.example.network.model

data class TrackResponseDto(
    val headers: HeaderDto,
    var results: List<TrackDto>,
)
