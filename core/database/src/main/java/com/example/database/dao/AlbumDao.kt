package com.example.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entities.FavoriteAlbum

@Dao
interface AlbumDao {
    @Query("SELECT * FROM album ORDER BY id DESC")
    suspend fun getAllAlbums() : List<FavoriteAlbum>

    @Query("SELECT count(*) FROM album WHERE id = :albumId")
    suspend fun isFavorite(albumId: String) : Int

    @Delete
    suspend fun deleteAlbum(album: FavoriteAlbum)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlbum(album: FavoriteAlbum)
}