package com.example.home.data.repository

import com.example.home.data.mapper.TrackCellsMapper
import com.example.home.data.provider.ClientIdProvider
import com.example.home.domain.HomeRepository
import com.example.home.domain.model.TrackCell
import com.example.network.ApiHelper
import javax.inject.Inject

internal class HomeRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val trackCellsMapper: TrackCellsMapper,
    private val clientIdProvider: ClientIdProvider,
) : HomeRepository {

    override suspend fun getNewTracks(): List<TrackCell> =
        trackCellsMapper.mapListDtoToEntity(
            apiHelper.getTracks(
                clientId = clientIdProvider.getClientId(),
                id = "",
                artistId = "",
                albumId = "",
                namesearch = "",
                tags = "",
                order = "releasedate_desc",
                limit = 3,
                offset = 0
            )
        )

}