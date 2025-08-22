package com.example.playlist.data.repository

import com.example.database.dao.PlaylistDao
import com.example.database.entities.FavoritePlaylist
import com.example.network.ApiHelper
import com.example.playlist.data.mapper.PlaylistsMapper
import com.example.playlist.data.provider.ClientIdProvider
import com.example.playlist.domain.PlaylistRepository
import com.example.playlist.domain.model.Playlist
import javax.inject.Inject

internal class PlaylistRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val playlistsMapper: PlaylistsMapper,
    private val clientIdProvider: ClientIdProvider,
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override suspend fun getPlaylist(id: String): List<Playlist> =
        playlistsMapper.mapListDtoToEntity(
            apiHelper.getPlaylist(
                clientIdProvider.getClientId(),
                id
            )
        )

    override suspend fun isFavorite(id: String): Boolean {
        return playlistDao.isFavorite(id) != 0
    }

    override suspend fun favor(id: String) =
        playlistDao.insertPlaylist(FavoritePlaylist(id = id))

    override suspend fun disfavor(id: String) =
        playlistDao.deletePlaylist(FavoritePlaylist(id = id))

}