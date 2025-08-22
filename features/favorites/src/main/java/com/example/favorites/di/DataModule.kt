package com.example.favorites.di

import com.example.favorites.data.repository.FavoritesRepositoryImpl
import com.example.favorites.domain.FavoritesRepository
import dagger.Binds
import dagger.Module

@Module
internal interface DataModule {

    @Binds
    fun bindRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

}