package com.example.search.domain.model

data class Artist(
    val id: String,
    val name: String,
    val website: String,
    val joindate: String,
    val image: String,
    val shorturl: String,
    val shareurl: String,
    val tracks: List<TrackCell>
)
