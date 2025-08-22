package com.example.auth.domain.usecases

import com.example.auth.domain.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String) =
        repository.resetPassword(email)
}