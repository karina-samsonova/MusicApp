package com.example.search.di

import com.example.search.BuildConfig
import com.example.search.data.provider.ClientIdProvider
import dagger.Module
import dagger.Provides

@Module
internal class ClientIdModule {

    @Provides
    fun provideClientId(): String {
        return BuildConfig.CLIENT_ID
    }

    @Provides
    fun provideClientIdProvider(clientId: String): ClientIdProvider {
        return ClientIdProvider(clientId)
    }
}