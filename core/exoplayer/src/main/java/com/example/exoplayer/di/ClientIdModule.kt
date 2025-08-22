package com.example.exoplayer.di

import com.example.exoplayer.BuildConfig
import com.example.exoplayer.data.provider.ClientIdProvider
import dagger.Module
import dagger.Provides

@Module
class ClientIdModule {

    @Provides
    fun provideClientId(): String {
        return BuildConfig.CLIENT_ID
    }

    @Provides
    fun provideClientIdProvider(clientId: String): ClientIdProvider {
        return ClientIdProvider(clientId)
    }
}