package com.example.search.data.repository

import com.example.network.ApiHelper
import com.example.search.data.mapper.AlbumsMapper
import com.example.search.data.mapper.ArtistsMapper
import com.example.search.data.mapper.AutocompleteMapper
import com.example.search.data.mapper.PlaylistsMapper
import com.example.search.data.mapper.TrackCellsMapper
import com.example.search.data.provider.ClientIdProvider
import com.example.search.domain.SearchRepository
import com.example.search.domain.model.AlbumResponse
import com.example.search.domain.model.ArtistResponse
import com.example.search.domain.model.ContentType
import com.example.search.domain.model.PlaylistResponse
import com.example.search.domain.model.TrackResponse
import javax.inject.Inject

internal class SearchRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val autocompleteMapper: AutocompleteMapper,
    private val trackCellsMapper: TrackCellsMapper,
    private val artistsMapper: ArtistsMapper,
    private val albumsMapper: AlbumsMapper,
    private val playlistsMapper: PlaylistsMapper,
    private val clientIdProvider: ClientIdProvider
) : SearchRepository {

    override suspend fun getAutocomplete(prefix: String, type: ContentType): List<String> =
        autocompleteMapper.mapListDtoToEntity(
            apiHelper.getAutocomplete(
                clientIdProvider.getClientId(), prefix
            ),
            type
        )

    override suspend fun getTracks(
        id: String,
        namesearch: String,
        limit: Int,
        offset: Int?
    ): TrackResponse =
        trackCellsMapper.mapListDtoToEntity(
            apiHelper.getTracks(
                clientId = clientIdProvider.getClientId(),
                id = id,
                artistId = "",
                albumId = "",
                namesearch = namesearch,
                tags = "",
                order = "popularity_total",
                limit = limit,
                offset = offset
            )
        )

    override suspend fun getArtists(
        id: String,
        namesearch: String,
        limit: Int,
        offset: Int?
    ): ArtistResponse =
        artistsMapper.mapListDtoToEntity(
            apiHelper.getArtists(
                clientId = clientIdProvider.getClientId(),
                id = id,
                namesearch = namesearch,
                order = "popularity_total",
                limit = limit,
                offset = offset
            )
        )

    override suspend fun getAlbums(
        id: String,
        namesearch: String,
        limit: Int,
        offset: Int?
    ): AlbumResponse =
        albumsMapper.mapListDtoToEntity(
            apiHelper.getAlbums(
                clientId = clientIdProvider.getClientId(),
                id = id,
                namesearch = namesearch,
                order = "popularity_total",
                limit = limit,
                offset = offset
            )
        )

    override suspend fun getPlaylists(
        id: String,
        namesearch: String,
        limit: Int,
        offset: Int?
    ): PlaylistResponse =
        playlistsMapper.mapListDtoToEntity(
            apiHelper.getPlaylists(
                clientId = clientIdProvider.getClientId(),
                id = id,
                namesearch = namesearch,
                order = "creationdate_desc",
                limit = limit,
                offset = offset
            )
        )
}