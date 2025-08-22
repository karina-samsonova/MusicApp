package com.example.database.di

import android.content.Context
import com.example.database.FavoritesDatabase
import com.example.database.dao.AlbumDao
import com.example.database.dao.ArtistDao
import com.example.database.dao.PlaylistDao
import com.example.database.dao.TrackDao
import dagger.BindsInstance
import dagger.Component

@Component(modules = [DatabaseModule::class])
interface DatabaseComponent {
    fun favoritesDatabase(): FavoritesDatabase
    fun trackDao(): TrackDao
    fun albumDao(): AlbumDao
    fun artistDao(): ArtistDao
    fun playlistDao(): PlaylistDao

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): DatabaseComponent
    }
}