package com.example.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entities.FavoriteArtist

@Dao
interface ArtistDao {
    @Query("SELECT * FROM artist ORDER BY id DESC")
    suspend fun getAllArtists() : List<FavoriteArtist>

    @Query("SELECT count(*) FROM artist WHERE id = :artistId")
    suspend fun isFavorite(artistId: String) : Int

    @Delete
    suspend fun deleteArtist(artist: FavoriteArtist)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtist(artist: FavoriteArtist)
}