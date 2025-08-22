package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.database.dao.AlbumDao
import com.example.database.dao.ArtistDao
import com.example.database.dao.PlaylistDao
import com.example.database.dao.TrackDao
import com.example.database.entities.FavoriteAlbum
import com.example.database.entities.FavoriteArtist
import com.example.database.entities.FavoritePlaylist
import com.example.database.entities.FavoriteTrack

@Database(
    entities = [FavoriteTrack::class, FavoriteAlbum::class, FavoriteArtist::class, FavoritePlaylist::class],
    version = 4,
    exportSchema = false
)
abstract class FavoritesDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var db: FavoritesDatabase? = null
        private const val DB_NAME = "favorites.db"
        private val LOCK = Any()

        fun getInstance(context: Context): FavoritesDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance = Room.databaseBuilder(
                    context,
                    FavoritesDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                db = instance
                return instance
            }
        }
    }

    abstract fun trackDao(): TrackDao

    abstract fun albumDao(): AlbumDao

    abstract fun artistDao(): ArtistDao

    abstract fun playlistDao(): PlaylistDao

}