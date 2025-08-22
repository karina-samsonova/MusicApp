package com.example.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entities.FavoriteTrack

@Dao
interface TrackDao {
    @Query("SELECT * FROM track")
    suspend fun getAllTracks() : List<FavoriteTrack>

    @Query("SELECT count(*) FROM track WHERE id = :trackId")
    suspend fun isFavorite(trackId: String) : Int

    @Delete
    suspend fun deleteTrack(track: FavoriteTrack)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: FavoriteTrack)
}