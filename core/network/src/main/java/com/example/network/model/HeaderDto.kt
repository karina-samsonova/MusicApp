package com.example.network.model

data class HeaderDto(
    val status: String,
    val code: Int,
    val error_message: String,
    val warnings: String,
    val results_count: Int,
    val next: String?
)
