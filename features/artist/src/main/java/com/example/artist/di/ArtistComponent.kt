package com.example.artist.di

import com.example.artist.domain.usecases.DisfavorArtistUseCase
import com.example.artist.domain.usecases.FavorArtistUseCase
import com.example.artist.domain.usecases.GetAlbumsUseCase
import com.example.artist.domain.usecases.GetArtistUseCase
import com.example.artist.domain.usecases.GetTracksUseCase
import com.example.artist.domain.usecases.IsFavoriteUseCase
import com.example.artist.presentation.ui.ArtistFragment
import com.example.artist.presentation.viewModel.ArtistViewModel
import com.example.database.di.DatabaseComponent
import com.example.network.di.NetworkComponent
import dagger.Component

@Component(
    modules = [DataModule::class, ClientIdModule::class],
    dependencies = [NetworkComponent::class, DatabaseComponent::class]
)
interface ArtistComponent {
    fun inject(artistFragment: ArtistFragment)
    fun inject(viewModel: ArtistViewModel)
    fun getArtistUseCase(): GetArtistUseCase
    fun getAlbumsUseCase(): GetAlbumsUseCase
    fun getTracksUseCase(): GetTracksUseCase
    fun isFavoriteUseCase(): IsFavoriteUseCase
    fun favorArtistUseCase(): FavorArtistUseCase
    fun disfavorArtistUseCase(): DisfavorArtistUseCase

    @Component.Builder
    interface Builder {
        fun networkComponent(networkComponent: NetworkComponent): Builder
        fun databaseComponent(databaseComponent: DatabaseComponent): Builder
        fun build(): ArtistComponent
    }
}