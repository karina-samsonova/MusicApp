package com.example.search.di

import com.example.exoplayer.di.PlayerModule
import com.example.exoplayer.di.ViewModelModule
import com.example.network.di.NetworkComponent
import com.example.search.domain.usecases.GetAlbumsUseCase
import com.example.search.domain.usecases.GetArtistsUseCase
import com.example.search.domain.usecases.GetAutocompleteUseCase
import com.example.search.domain.usecases.GetPlaylistsUseCase
import com.example.search.domain.usecases.GetTracksUseCase
import com.example.search.presentation.ui.SearchFragment
import com.example.search.presentation.viewModel.SearchViewModel
import dagger.Component

@Component(
    modules = [DataModule::class, ClientIdModule::class, ViewModelModule::class, PlayerModule::class],
    dependencies = [NetworkComponent::class]
)
interface SearchComponent {
    fun inject(searchFragment: SearchFragment)
    fun inject(viewModel: SearchViewModel)
    fun getAutocompleteUseCase(): GetAutocompleteUseCase
    fun getTracksUseCase(): GetTracksUseCase
    fun getArtistsUseCase(): GetArtistsUseCase
    fun getAlbumsUseCase(): GetAlbumsUseCase
    fun getPlaylistsUseCase(): GetPlaylistsUseCase

    @Component.Builder
    interface Builder {
        fun networkComponent(networkComponent: NetworkComponent): Builder
        fun build(): SearchComponent
    }
}