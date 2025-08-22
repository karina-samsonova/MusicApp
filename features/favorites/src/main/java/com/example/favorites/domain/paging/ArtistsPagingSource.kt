package com.example.favorites.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.favorites.domain.model.Artist
import com.example.favorites.domain.usecases.GetArtistsUseCase
import com.example.favorites.domain.usecases.GetFavoriteArtistsUseCase

internal class ArtistsPagingSource(
    private val getArtistsUseCase: GetArtistsUseCase,
    private val getFavoruteArtistsUseCase: GetFavoriteArtistsUseCase,
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

            val ids = getFavoruteArtistsUseCase()
            val idStr = ids.joinToString("+")

            val response = getArtistsUseCase(
                id = idStr,
                namesearch = namesearch,
                limit = params.loadSize,
                offset = page * params.loadSize
            )

            response.content =
                response.content.sortedWith(compareByDescending { ids.indexOf(it.id) })

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