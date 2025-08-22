package com.example.musicapp.di

import com.example.database.di.DatabaseModule
import com.example.exoplayer.di.PlayerComponent
import com.example.musicapp.MainActivity
import com.example.musicapp.presentation.MainFragment
import com.example.network.di.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [NetworkModule::class, DatabaseModule::class],
    dependencies = [PlayerComponent::class]
)
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: MainFragment)

    @Component.Builder
    interface Builder {
        fun playerComponent(playerComponent: PlayerComponent): Builder
        fun build(): AppComponent
    }
}