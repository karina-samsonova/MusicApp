package com.example.network.model

data class PlaylistResponseDto(
    val headers: HeaderDto,
    val results: List<PlaylistDto>,
)
