package com.example.exoplayer.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.media3.exoplayer.ExoPlayer
import com.example.database.di.DatabaseComponent
import com.example.exoplayer.domain.usecases.DisfavorTrackUseCase
import com.example.exoplayer.domain.usecases.FavorTrackUseCase
import com.example.exoplayer.domain.usecases.GetTracksUseCase
import com.example.exoplayer.domain.usecases.IsFavoriteUseCase
import com.example.network.di.NetworkComponent
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [PlayerModule::class, ClientIdModule::class, ViewModelModule::class],
    dependencies = [NetworkComponent::class, DatabaseComponent::class]
)
interface PlayerComponent {
    fun exoPlayer(): ExoPlayer
    fun getTracksUseCase(): GetTracksUseCase
    fun isFavoriteUseCase(): IsFavoriteUseCase
    fun favorTrackUseCase(): FavorTrackUseCase
    fun disfavorTrackUseCase(): DisfavorTrackUseCase
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Application): Builder
        fun networkComponent(networkComponent: NetworkComponent): Builder
        fun databaseComponent(databaseComponent: DatabaseComponent): Builder
        fun build(): PlayerComponent
    }
}