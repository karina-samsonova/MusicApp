package com.example.album.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.album.domain.model.Album
import com.example.album.domain.model.TrackCell
import com.example.album.domain.paging.TracksPagingSource
import com.example.album.domain.usecases.DisfavorAlbumUseCase
import com.example.album.domain.usecases.FavorAlbumUseCase
import com.example.album.domain.usecases.GetAlbumUseCase
import com.example.album.domain.usecases.GetTracksUseCase
import com.example.album.domain.usecases.IsFavoriteUseCase
import com.example.network.LoadingState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlbumViewModel : ViewModel() {

    @Inject
    lateinit var getAlbumUseCase: GetAlbumUseCase

    @Inject
    lateinit var getTracksUseCase: GetTracksUseCase

    @Inject
    lateinit var isFavoriteUseCase: IsFavoriteUseCase

    @Inject
    lateinit var favorAlbumUseCase: FavorAlbumUseCase

    @Inject
    lateinit var disfavorAlbumUseCase: DisfavorAlbumUseCase

    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> = _album

    private val _tracks = MutableStateFlow<PagingData<TrackCell>>(PagingData.empty())
    val tracks: StateFlow<PagingData<TrackCell>> = _tracks

    private val _searchLoadingStateLiveData = MutableLiveData<LoadingState>()
    val searchLoadingStateLiveData: LiveData<LoadingState> =
        _searchLoadingStateLiveData

    private val searchExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("AlbumViewModel", "Exception caught: $throwable")
        _searchLoadingStateLiveData.postValue(LoadingState.ERROR)
    }
    private val searchCoroutineScope =
        CoroutineScope(Dispatchers.IO + searchExceptionHandler)

    fun getAlbum(id: String) {
        _searchLoadingStateLiveData.postValue(LoadingState.LOADING)
        searchCoroutineScope.launch {
            getTracks(id)
            val albumInfo = getAlbumUseCase(id)
            albumInfo.is_favorite = isFavoriteUseCase(id)
            _album.postValue(albumInfo)
            _searchLoadingStateLiveData.postValue(LoadingState.SUCCESS)
        }
    }

    private fun getTracks(id: String) {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 10,
                    enablePlaceholders = false,
                    initialLoadSize = 20,
                ),
                pagingSourceFactory = {
                    TracksPagingSource(
                        getTracksUseCase,
                        id,
                    )
                }
            ).flow.cachedIn(viewModelScope).collect {
                _tracks.value = it
            }
        }
    }

    fun favorAlbum(id: String) {
        searchCoroutineScope.launch {
            favorAlbumUseCase(id)
        }
    }

    fun disfavorAlbum(id: String) {
        searchCoroutineScope.launch {
            disfavorAlbumUseCase(id)
        }
    }
}