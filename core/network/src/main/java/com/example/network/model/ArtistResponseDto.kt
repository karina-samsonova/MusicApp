package com.example.network.model

data class ArtistResponseDto(
    val headers: HeaderDto,
    val results: List<ArtistDto>,
)
