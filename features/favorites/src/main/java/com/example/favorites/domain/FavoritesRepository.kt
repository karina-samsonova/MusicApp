package com.example.favorites.domain

import com.example.favorites.domain.model.AlbumResponse
import com.example.favorites.domain.model.ArtistResponse
import com.example.favorites.domain.model.PlaylistResponse
import com.example.favorites.domain.model.TrackResponse

interface FavoritesRepository {

    suspend fun getTracks(id: String, namesearch: String, limit: Int, offset: Int?): TrackResponse

    suspend fun getArtists(id: String, namesearch: String, limit: Int, offset: Int?): ArtistResponse

    suspend fun getAlbums(id: String, namesearch: String, limit: Int, offset: Int?): AlbumResponse

    suspend fun getPlaylists(id: String, namesearch: String, limit: Int, offset: Int?): PlaylistResponse

    suspend fun getFavoriteTracks(): List<String>

    suspend fun getFavoriteAlbums(): List<String>

    suspend fun getFavoriteArtists(): List<String>

    suspend fun getFavoritePlaylists(): List<String>
}