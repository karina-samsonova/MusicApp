package com.example.exoplayer.data.repository

import com.example.database.dao.TrackDao
import com.example.database.entities.FavoriteTrack
import com.example.exoplayer.data.mapper.FavoriteTracksMapper
import com.example.exoplayer.data.mapper.TrackCellsMapper
import com.example.exoplayer.data.provider.ClientIdProvider
import com.example.exoplayer.domain.PlayerRepository
import com.example.exoplayer.domain.model.TrackCell
import com.example.network.ApiHelper
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val trackCellsMapper: TrackCellsMapper,
    private val favoriteTracksMapper: FavoriteTracksMapper,
    private val clientIdProvider: ClientIdProvider,
    private val trackDao: TrackDao
) : PlayerRepository {

    override suspend fun getTracks(
        id: String,
        artistId: String,
        albumId: String,
        namesearch: String,
        tags: String,
        limit: Int,
        offset: Int?,
        order: String
    ): List<TrackCell> =
        trackCellsMapper.mapListDtoToEntity(
            apiHelper.getTracks(
                clientId = clientIdProvider.getClientId(),
                id = id,
                artistId = artistId,
                albumId = albumId,
                namesearch = namesearch,
                tags = tags,
                order = order,
                limit = limit,
                offset = offset,
            )
        )

    override suspend fun getFavoriteTracks(): List<String> =
        favoriteTracksMapper.mapListEntityToString(trackDao.getAllTracks())

    override suspend fun isFavorite(id: String): Boolean {
        return trackDao.isFavorite(id) != 0
    }

    override suspend fun favor(id: String) =
        trackDao.insertTrack(FavoriteTrack(id = id))

    override suspend fun disfavor(id: String) =
        trackDao.deleteTrack(FavoriteTrack(id = id))
}