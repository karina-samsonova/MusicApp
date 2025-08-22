package com.example.artist.di

import com.example.artist.data.repository.ArtistRepositoryImpl
import com.example.artist.domain.ArtistRepository
import dagger.Binds
import dagger.Module

@Module
internal interface DataModule {

    @Binds
    fun bindRepository(impl: ArtistRepositoryImpl): ArtistRepository

}