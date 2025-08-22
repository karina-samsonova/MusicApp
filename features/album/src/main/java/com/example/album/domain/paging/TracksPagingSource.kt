package com.example.album.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.album.domain.model.TrackCell
import com.example.album.domain.usecases.GetTracksUseCase

internal class TracksPagingSource(
    private val getTracksUseCase: GetTracksUseCase,
    private val id: String,
) : PagingSource<Int, TrackCell>() {

    override fun getRefreshKey(state: PagingState<Int, TrackCell>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrackCell> {
        return try {
            val page = params.key ?: 0

            val response = getTracksUseCase(
                id = id,
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