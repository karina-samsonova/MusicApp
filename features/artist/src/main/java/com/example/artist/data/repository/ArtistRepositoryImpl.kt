package com.example.artist.data.repository

import com.example.artist.data.mapper.AlbumsMapper
import com.example.artist.data.mapper.ArtistsMapper
import com.example.artist.data.mapper.TrackCellsMapper
import com.example.artist.data.provider.ClientIdProvider
import com.example.artist.domain.ArtistRepository
import com.example.artist.domain.model.AlbumResponse
import com.example.artist.domain.model.Artist
import com.example.artist.domain.model.TrackResponse
import com.example.database.dao.ArtistDao
import com.example.database.entities.FavoriteArtist
import com.example.network.ApiHelper
import javax.inject.Inject

internal class ArtistRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val trackCellsMapper: TrackCellsMapper,
    private val albumsMapper: AlbumsMapper,
    private val artistMapper: ArtistsMapper,
    private val clientIdProvider: ClientIdProvider,
    private val artistDao: ArtistDao
) : ArtistRepository {

    override suspend fun getArtist(id: String): Artist =
        artistMapper.mapListDtoToEntity(
            apiHelper.getArtists(
                clientId = clientIdProvider.getClientId(),
                id = id,
                namesearch = "",
                order = "",
                limit = 1,
                offset = 0
            )
        )

    override suspend fun getArtistTracks(id: String, limit: Int, offset: Int?): TrackResponse =
        trackCellsMapper.mapListDtoToEntity(
            apiHelper.getArtistTracks(
                clientId = clientIdProvider.getClientId(),
                id = id,
                limit = limit,
                offset = offset
            )
        )

    override suspend fun getArtistAlbums(id: String, limit: Int, offset: Int?): AlbumResponse =
        albumsMapper.mapListDtoToEntity(
            apiHelper.getArtistAlbums(
                clientId = clientIdProvider.getClientId(),
                id = id,
                limit = limit,
                offset = offset
            )
        )

    override suspend fun isFavorite(id: String): Boolean {
        return artistDao.isFavorite(id) != 0
    }

    override suspend fun favor(id: String) =
        artistDao.insertArtist(FavoriteArtist(id = id))

    override suspend fun disfavor(id: String) =
        artistDao.deleteArtist(FavoriteArtist(id = id))
}