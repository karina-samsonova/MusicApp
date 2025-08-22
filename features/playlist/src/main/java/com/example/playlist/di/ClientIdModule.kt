package com.example.playlist.di

import com.example.playlist.BuildConfig
import com.example.playlist.data.provider.ClientIdProvider
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