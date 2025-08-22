package com.example.favorites.di

import com.example.database.di.DatabaseComponent
import com.example.favorites.domain.usecases.GetFavoriteTracksUseCase
import com.example.favorites.domain.usecases.GetTracksUseCase
import com.example.favorites.domain.usecases.GetAlbumsUseCase
import com.example.favorites.domain.usecases.GetArtistsUseCase
import com.example.favorites.domain.usecases.GetFavoriteAlbumsUseCase
import com.example.favorites.domain.usecases.GetFavoriteArtistsUseCase
import com.example.favorites.domain.usecases.GetFavoritePlaylistsUseCase
import com.example.favorites.domain.usecases.GetPlaylistsUseCase
import com.example.favorites.presentation.ui.FavoritesFragment
import com.example.favorites.presentation.viewModel.FavoritesViewModel
import com.example.network.di.NetworkComponent
import dagger.Component

@Component(
    modules = [DataModule::class, ClientIdModule::class],
    dependencies = [NetworkComponent::class, DatabaseComponent::class]
)
interface FavoritesComponent {
    fun inject(favoritesFragment: FavoritesFragment)
    fun inject(viewModel: FavoritesViewModel)
    fun getTracksUseCase(): GetTracksUseCase
    fun getArtistsUseCase(): GetArtistsUseCase
    fun getAlbumsUseCase(): GetAlbumsUseCase
    fun getPlaylistsUseCase(): GetPlaylistsUseCase
    fun getFavoriteTracksUseCase(): GetFavoriteTracksUseCase
    fun getFavoriteAlbumsUseCase(): GetFavoriteAlbumsUseCase
    fun getFavoriteArtistsUseCase(): GetFavoriteArtistsUseCase
    fun getFavoritePlaylistsUseCase(): GetFavoritePlaylistsUseCase

    @Component.Builder
    interface Builder {
        fun networkComponent(networkComponent: NetworkComponent): Builder
        fun databaseComponent(databaseComponent: DatabaseComponent): Builder
        fun build(): FavoritesComponent
    }
}