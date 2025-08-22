package com.example.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entities.FavoritePlaylist

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    suspend fun getAllPlaylists(): List<FavoritePlaylist>

    @Query("SELECT count(*) FROM playlist WHERE id = :playlistId")
    suspend fun isFavorite(playlistId: String) : Int

    @Delete
    suspend fun deletePlaylist(playlist: FavoritePlaylist)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: FavoritePlaylist)
}