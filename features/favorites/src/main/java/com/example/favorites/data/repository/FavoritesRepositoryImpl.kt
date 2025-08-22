package com.example.favorites.data.repository

import com.example.database.dao.AlbumDao
import com.example.database.dao.ArtistDao
import com.example.database.dao.PlaylistDao
import com.example.database.dao.TrackDao
import com.example.favorites.data.mapper.AlbumsMapper
import com.example.favorites.data.mapper.ArtistsMapper
import com.example.favorites.data.mapper.FavoriteAlbumsMapper
import com.example.favorites.data.mapper.FavoriteArtistsMapper
import com.example.favorites.data.mapper.FavoritePlaylistsMapper
import com.example.favorites.data.mapper.FavoriteTracksMapper
import com.example.favorites.data.mapper.PlaylistsMapper
import com.example.favorites.data.mapper.TrackCellsMapper
import com.example.favorites.data.provider.ClientIdProvider
import com.example.favorites.domain.FavoritesRepository
import com.example.network.ApiHelper
import com.example.favorites.domain.model.AlbumResponse
import com.example.favorites.domain.model.ArtistResponse
import com.example.favorites.domain.model.PlaylistResponse
import com.example.favorites.domain.model.TrackResponse
import javax.inject.Inject

internal class FavoritesRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val favoriteTracksMapper: FavoriteTracksMapper,
    private val favoriteAlbumsMapper: FavoriteAlbumsMapper,
    private val favoriteArtistsMapper: FavoriteArtistsMapper,
    private val favoritePlaylistsMapper: FavoritePlaylistsMapper,
    private val trackCellsMapper: TrackCellsMapper,
    private val artistsMapper: ArtistsMapper,
    private val albumsMapper: AlbumsMapper,
    private val playlistsMapper: PlaylistsMapper,
    private val clientIdProvider: ClientIdProvider,
    private val trackDao: TrackDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val playlistDao: PlaylistDao,
) : FavoritesRepository {

    override suspend fun getTracks(
        id: String,
        namesearch: String,
        limit: Int,
        offset: Int?
    ): TrackResponse =
        if (id == "")
            TrackResponse(next = null, content = emptyList())
        else
            trackCellsMapper.mapListDtoToEntity(
                apiHelper.getTracks(
                    clientId = clientIdProvider.getClientId(),
                    id = id,
                    artistId = "",
                    albumId = "",
                    namesearch = namesearch,
                    tags = "",
                    order = "",
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
        if (id == "")
            ArtistResponse(next = null, content = emptyList())
        else
            artistsMapper.mapListDtoToEntity(
                apiHelper.getArtists(
                    clientId = clientIdProvider.getClientId(),
                    id = id,
                    namesearch = namesearch,
                    order = "",
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
        if (id == "")
            AlbumResponse(next = null, content = emptyList())
        else
            albumsMapper.mapListDtoToEntity(
                apiHelper.getAlbums(
                    clientId = clientIdProvider.getClientId(),
                    id = id,
                    namesearch = namesearch,
                    order = "",
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
        if (id == "")
            PlaylistResponse(next = null, content = emptyList())
        else
            playlistsMapper.mapListDtoToEntity(
                apiHelper.getPlaylists(
                    clientId = clientIdProvider.getClientId(),
                    id = id,
                    namesearch = namesearch,
                    order = "",
                    limit = limit,
                    offset = offset
                )
            )

    override suspend fun getFavoriteTracks(): List<String> =
        favoriteTracksMapper.mapListEntityToString(trackDao.getAllTracks())

    override suspend fun getFavoriteAlbums(): List<String> =
        favoriteAlbumsMapper.mapListEntityToString(albumDao.getAllAlbums())

    override suspend fun getFavoriteArtists(): List<String> =
        favoriteArtistsMapper.mapListEntityToString(artistDao.getAllArtists())

    override suspend fun getFavoritePlaylists(): List<String> =
        favoritePlaylistsMapper.mapListEntityToString(playlistDao.getAllPlaylists())

}