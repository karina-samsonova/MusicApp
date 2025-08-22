package com.example.auth.di

import com.example.auth.domain.usecases.IsUserLoggedInUseCase
import com.example.auth.domain.usecases.LoginUseCase
import com.example.auth.domain.usecases.LogoutUseCase
import com.example.auth.domain.usecases.RegisterUseCase
import com.example.auth.domain.usecases.ResetPasswordUseCase
import com.example.auth.presentation.ui.LoginFragment
import com.example.auth.presentation.ui.RegisterFragment
import com.example.auth.presentation.ui.ResetPasswordFragment
import com.example.auth.presentation.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.Component

@Component(modules = [AuthModule::class])
interface AuthComponent {
    fun inject(loginFragment: LoginFragment)
    fun inject(registerFragment: RegisterFragment)
    fun inject(resetPasswordFragment: ResetPasswordFragment)
    fun inject(viewModel: AuthViewModel)
    fun firebaseAuth(): FirebaseAuth
    fun isUserLoggedInUseCase(): IsUserLoggedInUseCase
    fun loginUseCase(): LoginUseCase
    fun logoutUseCase(): LogoutUseCase
    fun registerUseCase(): RegisterUseCase
    fun resetPasswordUseCase(): ResetPasswordUseCase

    @Component.Builder
    interface Builder {
        fun build(): AuthComponent
    }
}