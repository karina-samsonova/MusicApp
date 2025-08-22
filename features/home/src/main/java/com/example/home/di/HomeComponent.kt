package com.example.home.di

import com.example.home.domain.usecases.GetNewTracksUseCase
import com.example.home.presentation.ui.HomeFragment
import com.example.home.presentation.viewModel.HomeViewModel
import com.example.network.di.NetworkComponent
import dagger.Component

@Component(
    modules = [DataModule::class, ClientIdModule::class],
    dependencies = [NetworkComponent::class]
)
interface HomeComponent {
    fun inject(homeFragment: HomeFragment)
    fun inject(homeViewModel: HomeViewModel)
    fun getNewTracksUseCase(): GetNewTracksUseCase

    @Component.Builder
    interface Builder {
        fun networkComponent(networkComponent: NetworkComponent): Builder
        fun build(): HomeComponent
    }
}