package com.example.playlist.di

import com.example.playlist.data.repository.PlaylistRepositoryImpl
import com.example.playlist.domain.PlaylistRepository
import dagger.Binds
import dagger.Module

@Module
internal interface DataModule {

    @Binds
    fun bindRepository(impl: PlaylistRepositoryImpl): PlaylistRepository

}