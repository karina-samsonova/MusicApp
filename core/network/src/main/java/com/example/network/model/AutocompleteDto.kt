package com.example.network.model

data class AutocompleteDto(
    val tags: List<MatchDto>?,
    val artists: List<MatchDto>?,
    val tracks: List<MatchDto>?,
    val albums: List<MatchDto>?,
)
