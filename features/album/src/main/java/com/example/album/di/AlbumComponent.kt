package com.example.album.di

import com.example.album.domain.usecases.DisfavorAlbumUseCase
import com.example.album.domain.usecases.FavorAlbumUseCase
import com.example.album.domain.usecases.GetAlbumUseCase
import com.example.album.domain.usecases.GetTracksUseCase
import com.example.album.domain.usecases.IsFavoriteUseCase
import com.example.album.presentation.ui.AlbumFragment
import com.example.album.presentation.viewModel.AlbumViewModel
import com.example.database.di.DatabaseComponent
import com.example.network.di.NetworkComponent
import dagger.Component

@Component(
    modules = [DataModule::class, ClientIdModule::class],
    dependencies = [NetworkComponent::class, DatabaseComponent::class]
)
interface AlbumComponent {
    fun inject(albumFragment: AlbumFragment)
    fun inject(viewModel: AlbumViewModel)
    fun getAlbumUseCase(): GetAlbumUseCase
    fun getTracksUseCase(): GetTracksUseCase
    fun isFavoriteUseCase(): IsFavoriteUseCase
    fun favorAlbumUseCase(): FavorAlbumUseCase
    fun disfavorAlbumUseCase(): DisfavorAlbumUseCase

    @Component.Builder
    interface Builder {
        fun networkComponent(networkComponent: NetworkComponent): Builder
        fun databaseComponent(databaseComponent: DatabaseComponent): Builder
        fun build(): AlbumComponent
    }
}