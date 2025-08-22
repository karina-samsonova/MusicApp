package com.example.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "album")
data class FavoriteAlbum(
    @ColumnInfo(name = "id", index = true)
    @PrimaryKey
    val id: String = ""
): Serializable
