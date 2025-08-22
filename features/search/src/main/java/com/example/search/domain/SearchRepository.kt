package com.example.search.domain

import com.example.search.domain.model.AlbumResponse
import com.example.search.domain.model.ArtistResponse
import com.example.search.domain.model.ContentType
import com.example.search.domain.model.PlaylistResponse
import com.example.search.domain.model.TrackResponse

interface SearchRepository {

    suspend fun getAutocomplete(prefix: String, type: ContentType): List<String>

    suspend fun getTracks(id: String, namesearch: String, limit: Int, offset: Int?): TrackResponse

    suspend fun getArtists(id: String, namesearch: String, limit: Int, offset: Int?): ArtistResponse

    suspend fun getAlbums(id: String, namesearch: String, limit: Int, offset: Int?): AlbumResponse

    suspend fun getPlaylists(id: String, namesearch: String, limit: Int, offset: Int?): PlaylistResponse
}