package com.example.auth.di

import com.example.auth.data.repository.AuthRepositoryImpl
import com.example.auth.domain.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
internal interface AuthModule {

    @Binds
    fun bindRepository(impl: AuthRepositoryImpl): AuthRepository

    companion object {
        @Provides
        fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth
    }
}