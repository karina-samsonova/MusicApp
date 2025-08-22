package com.example.search.domain.model

data class AlbumResponse(
    val next: String?,
    val content: List<Album>
)
