package com.example.exoplayer.di

import android.app.Application
import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.example.exoplayer.data.repository.PlayerRepositoryImpl
import com.example.exoplayer.domain.PlayerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface PlayerModule {

    @Binds
    fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository

    companion object {
        @Provides
        fun provideExoPlayer(context: Application): ExoPlayer {
            return ExoPlayer.Builder(context).build()
        }
    }
}