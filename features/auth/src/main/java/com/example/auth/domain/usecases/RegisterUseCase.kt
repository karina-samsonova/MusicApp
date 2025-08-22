package com.example.auth.domain.usecases

import com.example.auth.domain.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        repository.register(email, password)
}