package com.example.search.di

import com.example.search.data.repository.SearchRepositoryImpl
import com.example.search.domain.SearchRepository
import dagger.Binds
import dagger.Module

@Module
internal interface DataModule {

    @Binds
    fun bindRepository(impl: SearchRepositoryImpl): SearchRepository

}