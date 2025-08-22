package com.example.artist.di

import com.example.artist.BuildConfig
import com.example.artist.data.provider.ClientIdProvider
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