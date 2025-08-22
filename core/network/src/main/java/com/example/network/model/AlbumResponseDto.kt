package com.example.network.model

data class AlbumResponseDto(
    val headers: HeaderDto,
    val results: List<AlbumDto>,
)
