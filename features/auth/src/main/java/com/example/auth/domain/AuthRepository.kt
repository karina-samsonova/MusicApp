package com.example.auth.domain

import kotlin.Result

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    fun isUserLoggedIn(): Boolean
    fun logout()
}