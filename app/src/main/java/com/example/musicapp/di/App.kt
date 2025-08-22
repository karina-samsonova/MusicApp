package com.example.musicapp.di

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.database.di.DaggerDatabaseComponent
import com.example.database.di.DatabaseComponent
import com.example.exoplayer.di.DaggerPlayerComponent
import com.example.exoplayer.di.PlayerComponent
import com.example.network.di.DaggerNetworkComponent
import com.example.settings.domain.model.ThemePrefs
import com.google.firebase.FirebaseApp

class App : Application() {

    private lateinit var playerComponent: PlayerComponent
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(ThemePrefs.getThemeMode(this))

        FirebaseApp.initializeApp(this)

        val networkComponent = DaggerNetworkComponent.builder().build()

        val databaseComponent: DatabaseComponent =
            DaggerDatabaseComponent.builder().context(this).build()

        playerComponent = DaggerPlayerComponent.builder()
            .context(this)
            .networkComponent(networkComponent)
            .databaseComponent(databaseComponent)
            .build()

        appComponent = DaggerAppComponent.builder().playerComponent(playerComponent)
            .build()
    }
}