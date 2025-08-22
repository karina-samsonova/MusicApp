package com.example.auth.domain.usecases

import com.example.auth.domain.AuthRepository
import javax.inject.Inject

class IsUserLoggedInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.isUserLoggedIn()
}