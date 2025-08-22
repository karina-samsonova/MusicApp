package com.example.network

class ApiHelper(private val apiService: ApiService) {

    suspend fun getAutocomplete(clientId: String, prefix: String) =
        apiService.getAutocomplete(clientId, prefix)

    suspend fun getTracks(
        clientId: String,
        id: String,
        artistId: String,
        albumId: String,
        namesearch: String,
        tags: String,
        order: String,
        limit: Int,
        offset: Int?
    ) = apiService.getTracks(
        clientId = clientId,
        id = id,
        artist_id = artistId,
        album_id = albumId,
        namesearch = namesearch,
        tags = tags,
        order = order,
        limit = limit,
        offset = offset
    ).apply { results.filter { it.audio != "" } }

    suspend fun getArtists(
        clientId: String,
        id: String,
        namesearch: String,
        order: String,
        limit: Int,
        offset: Int?
    ) =
        apiService.getArtists(
            clientId = clientId,
            id = id,
            namesearch = namesearch,
            order = order,
            limit = limit,
            offset = offset
        )

    suspend fun getAlbums(
        clientId: String,
        id: String,
        namesearch: String,
        order: String,
        limit: Int,
        offset: Int?
    ) =
        apiService.getAlbums(
            clientId = clientId,
            id = id,
            namesearch = namesearch,
            order = order,
            limit = limit,
            offset = offset
        )

    suspend fun getPlaylists(
        clientId: String,
        id: String,
        namesearch: String,
        order: String,
        limit: Int,
        offset: Int?
    ) =
        apiService.getPlaylists(
            clientId = clientId,
            id = id,
            namesearch = namesearch,
            order = order,
            limit = limit,
            offset = offset
        )

    suspend fun getArtistTracks(clientId: String, id: String, limit: Int, offset: Int?) =
        apiService.getArtistTracks(clientId, id, limit, offset)

    suspend fun getArtistAlbums(clientId: String, id: String, limit: Int, offset: Int?) =
        apiService.getArtistAlbums(clientId, id, limit, offset)

    suspend fun getAlbumTracks(clientId: String, id: String, limit: Int, offset: Int?) =
        apiService.getAlbumTracks(clientId, id, limit, offset)

    suspend fun getPlaylist(clientId: String, id: String) =
        apiService.getPlaylist(clientId, id)


}