package com.example.auth.domain

sealed class AuthState {
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String?) : AuthState()
}