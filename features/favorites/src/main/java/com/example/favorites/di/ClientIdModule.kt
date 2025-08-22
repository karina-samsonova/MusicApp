package com.example.favorites.di

import com.example.favorites.BuildConfig
import com.example.favorites.data.provider.ClientIdProvider
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