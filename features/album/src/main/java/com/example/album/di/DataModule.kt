package com.example.album.di

import com.example.album.data.repository.AlbumRepositoryImpl
import com.example.album.domain.AlbumRepository
import dagger.Binds
import dagger.Module

@Module
internal interface DataModule {

    @Binds
    fun bindRepository(impl: AlbumRepositoryImpl): AlbumRepository

}