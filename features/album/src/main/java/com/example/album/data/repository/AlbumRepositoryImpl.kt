package com.example.album.data.repository

import com.example.album.data.mapper.AlbumsMapper
import com.example.album.data.mapper.TrackCellsMapper
import com.example.album.data.provider.ClientIdProvider
import com.example.album.domain.AlbumRepository
import com.example.album.domain.model.Album
import com.example.album.domain.model.TrackResponse
import com.example.database.dao.AlbumDao
import com.example.database.entities.FavoriteAlbum
import com.example.network.ApiHelper
import javax.inject.Inject

internal class AlbumRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val albumsMapper: AlbumsMapper,
    private val trackCellsMapper: TrackCellsMapper,
    private val clientIdProvider: ClientIdProvider,
    private val albumDao: AlbumDao
) : AlbumRepository {

    override suspend fun getAlbum(id: String): Album =
        albumsMapper.mapListDtoToEntity(
            apiHelper.getAlbums(
                clientId = clientIdProvider.getClientId(),
                id = id,
                namesearch = "",
                order = "",
                limit = 1,
                offset = 0
            )
        )

    override suspend fun getAlbumTracks(id: String, limit: Int, offset: Int?): TrackResponse =
        trackCellsMapper.mapListDtoToEntity(
            apiHelper.getAlbumTracks(
                clientId = clientIdProvider.getClientId(),
                id = id,
                limit = limit,
                offset = offset
            )
        )

    override suspend fun isFavorite(id: String): Boolean {
        return albumDao.isFavorite(id) != 0
    }

    override suspend fun favor(id: String) =
        albumDao.insertAlbum(FavoriteAlbum(id = id))

    override suspend fun disfavor(id: String) =
        albumDao.deleteAlbum(FavoriteAlbum(id = id))
}