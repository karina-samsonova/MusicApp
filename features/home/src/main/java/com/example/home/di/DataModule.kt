package com.example.home.di

import com.example.home.data.repository.HomeRepositoryImpl
import com.example.home.domain.HomeRepository
import dagger.Binds
import dagger.Module

@Module
internal interface DataModule {

    @Binds
    fun bindRepository(impl: HomeRepositoryImpl): HomeRepository

}