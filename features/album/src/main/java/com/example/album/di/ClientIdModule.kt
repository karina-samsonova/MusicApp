package com.example.album.di

import com.example.album.BuildConfig
import com.example.album.data.provider.ClientIdProvider
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