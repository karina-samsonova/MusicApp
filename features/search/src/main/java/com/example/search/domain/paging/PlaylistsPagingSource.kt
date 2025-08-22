package com.example.search.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.search.domain.model.Playlist
import com.example.search.domain.usecases.GetPlaylistsUseCase

internal class PlaylistsPagingSource(
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val id: String,
    private val namesearch: String
) : PagingSource<Int, Playlist>() {

    override fun getRefreshKey(state: PagingState<Int, Playlist>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Playlist> {
        return try {
            val page = params.key ?: 0

            val response = getPlaylistsUseCase(
                id = id,
                namesearch = namesearch,
                limit = params.loadSize,
                offset = page * params.loadSize
            )

            LoadResult.Page(
                data = response.content,
                prevKey = null,
                nextKey = if (response.next == null) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}