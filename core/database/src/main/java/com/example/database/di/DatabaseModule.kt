package com.example.database.di

import android.content.Context
import com.example.database.FavoritesDatabase
import com.example.database.dao.AlbumDao
import com.example.database.dao.ArtistDao
import com.example.database.dao.PlaylistDao
import com.example.database.dao.TrackDao
import dagger.Module
import dagger.Provides

@Module
object DatabaseModule {

    @Provides
    fun provideFavoritesDatabase(context: Context): FavoritesDatabase {
        return FavoritesDatabase.getInstance(context)
    }

    @Provides
    fun provideTrackDao(database: FavoritesDatabase): TrackDao {
        return database.trackDao()
    }

    @Provides
    fun provideAlbumDao(database: FavoritesDatabase): AlbumDao {
        return database.albumDao()
    }

    @Provides
    fun provideArtistDao(database: FavoritesDatabase): ArtistDao {
        return database.artistDao()
    }

    @Provides
    fun providePlaylistDao(database: FavoritesDatabase): PlaylistDao {
        return database.playlistDao()
    }
}