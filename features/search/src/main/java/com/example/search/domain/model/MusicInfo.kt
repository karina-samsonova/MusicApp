package com.example.search.domain.model

data class MusicInfo(
    val vocalinstrumental: String,
    val lang: String,
    val gender: String,
    val acousticelectric: String,
    val speed: String,
    val genres: List<String>,
    val instruments: List<String>,
    val vartags: List<String>,
)
