package com.example.search.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.search.domain.model.Artist
import com.example.search.domain.usecases.GetArtistsUseCase

internal class ArtistsPagingSource(
    private val getArtistsUseCase: GetArtistsUseCase,
    private val id: String,
    private val namesearch: String
) : PagingSource<Int, Artist>() {

    override fun getRefreshKey(state: PagingState<Int, Artist>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        return try {
            val page = params.key ?: 0

            val response = getArtistsUseCase(
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