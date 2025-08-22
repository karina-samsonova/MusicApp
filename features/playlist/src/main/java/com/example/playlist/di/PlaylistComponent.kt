package com.example.playlist.di

import com.example.database.di.DatabaseComponent
import com.example.network.di.NetworkComponent
import com.example.playlist.domain.usecases.DisfavorPlaylistUseCase
import com.example.playlist.domain.usecases.FavorPlaylistUseCase
import com.example.playlist.domain.usecases.GetPlaylistUseCase
import com.example.playlist.domain.usecases.IsFavoriteUseCase
import com.example.playlist.presentation.ui.PlaylistFragment
import com.example.playlist.presentation.viewModel.PlaylistViewModel
import dagger.Component

@Component(
    modules = [DataModule::class, ClientIdModule::class],
    dependencies = [NetworkComponent::class, DatabaseComponent::class]
)
interface PlaylistComponent {
    fun inject(playlistFragment: PlaylistFragment)
    fun inject(viewModel: PlaylistViewModel)
    fun getPlaylistUseCase(): GetPlaylistUseCase
    fun isFavoriteUseCase(): IsFavoriteUseCase
    fun favorPlaylistUseCase(): FavorPlaylistUseCase
    fun disfavorPlaylistUseCase(): DisfavorPlaylistUseCase

    @Component.Builder
    interface Builder {
        fun networkComponent(networkComponent: NetworkComponent): Builder
        fun databaseComponent(databaseComponent: DatabaseComponent): Builder
        fun build(): PlaylistComponent
    }
}