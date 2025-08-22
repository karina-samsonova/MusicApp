package com.example.network.model

data class MusicInfoDto(
    val vocalinstrumental: String,
    val lang: String,
    val gender: String,
    val acousticelectric: String,
    val speed: String,
    val tags: MusicInfoTagsDto
)
