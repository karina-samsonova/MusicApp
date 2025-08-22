package com.example.network

import com.example.network.model.AlbumResponseDto
import com.example.network.model.ArtistResponseDto
import com.example.network.model.AutocompleteResponseDto
import com.example.network.model.PlaylistResponseDto
import com.example.network.model.TrackResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("autocomplete")
    suspend fun getAutocomplete(
        @Query("client_id") clientId: String,
        @Query("prefix") prefix: String,
        @Query("format") format: String = "jsonpretty",
        @Query("limit") limit: Int = 3,
        @Query("matchcount") matchcount: Int = 1
    ): AutocompleteResponseDto

    @GET("tracks")
    suspend fun getTracks(
        @Query("client_id") clientId: String,
        @Query("id") id: String = "",
        @Query("artist_id") artist_id: String = "",
        @Query("album_id") album_id: String = "",
        @Query("namesearch") namesearch: String = "",
        @Query("tags") tags: String,
        @Query("order") order: String = "",
        @Query("limit") limit: Int,
        @Query("offset") offset: Int? = null,
        @Query("format") format: String = "jsonpretty",
        @Query("include") include: String = "musicinfo",
    ): TrackResponseDto

    @GET("artists")
    suspend fun getArtists(
        @Query("client_id") clientId: String,
        @Query("id") id: String = "",
        @Query("namesearch") namesearch: String = "",
        @Query("order") order: String = "",
        @Query("limit") limit: Int,
        @Query("offset") offset: Int? = null,
        @Query("format") format: String = "jsonpretty",
    ): ArtistResponseDto

    @GET("albums")
    suspend fun getAlbums(
        @Query("client_id") clientId: String,
        @Query("id") id: String = "",
        @Query("namesearch") namesearch: String = "",
        @Query("order") order: String = "",
        @Query("limit") limit: Int,
        @Query("offset") offset: Int? = null,
        @Query("format") format: String = "jsonpretty",
    ): AlbumResponseDto

    @GET("playlists")
    suspend fun getPlaylists(
        @Query("client_id") clientId: String,
        @Query("id") id: String = "",
        @Query("namesearch") namesearch: String = "",
        @Query("order") order: String = "",
        @Query("limit") limit: Int,
        @Query("offset") offset: Int? = null,
        @Query("format") format: String = "jsonpretty",
    ): PlaylistResponseDto

    @GET("tracks")
    suspend fun getArtistTracks(
        @Query("client_id") clientId: String,
        @Query("artist_id") id: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int? = null,
        @Query("format") format: String = "jsonpretty",
        @Query("order") order: String = "popularity_total",
    ): TrackResponseDto

    @GET("albums")
    suspend fun getArtistAlbums(
        @Query("client_id") clientId: String,
        @Query("artist_id") id: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int? = null,
        @Query("format") format: String = "jsonpretty",
        @Query("order") order: String = "popularity_total",
    ): AlbumResponseDto

    @GET("tracks")
    suspend fun getAlbumTracks(
        @Query("client_id") clientId: String,
        @Query("album_id") id: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int? = null,
        @Query("format") format: String = "jsonpretty",
        @Query("order") order: String = "popularity_total",
    ): TrackResponseDto

    @GET("playlists/tracks")
    suspend fun getPlaylist(
        @Query("client_id") clientId: String,
        @Query("id") id: String,
        @Query("format") format: String = "jsonpretty",
    ): PlaylistResponseDto

}