package com.example.search.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.search.domain.model.Album
import com.example.search.domain.usecases.GetAlbumsUseCase

internal class AlbumsPagingSource(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val id: String,
    private val namesearch: String
) : PagingSource<Int, Album>() {

    override fun getRefreshKey(state: PagingState<Int, Album>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        return try {
            val page = params.key ?: 0

            val response = getAlbumsUseCase(
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