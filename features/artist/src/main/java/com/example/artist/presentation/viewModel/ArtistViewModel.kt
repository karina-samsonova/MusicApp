package com.example.artist.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.artist.domain.model.Album
import com.example.artist.domain.model.Artist
import com.example.artist.domain.model.ContentType
import com.example.artist.domain.model.TrackCell
import com.example.artist.domain.paging.AlbumsPagingSource
import com.example.artist.domain.paging.TracksPagingSource
import com.example.artist.domain.usecases.DisfavorArtistUseCase
import com.example.artist.domain.usecases.FavorArtistUseCase
import com.example.artist.domain.usecases.GetAlbumsUseCase
import com.example.artist.domain.usecases.GetArtistUseCase
import com.example.artist.domain.usecases.GetTracksUseCase
import com.example.artist.domain.usecases.IsFavoriteUseCase
import com.example.network.LoadingState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArtistViewModel : ViewModel() {

    @Inject
    lateinit var getArtistUseCase: GetArtistUseCase

    @Inject
    lateinit var getTracksUseCase: GetTracksUseCase

    @Inject
    lateinit var getAlbumsUseCase: GetAlbumsUseCase

    @Inject
    lateinit var isFavoriteUseCase: IsFavoriteUseCase

    @Inject
    lateinit var favorArtistUseCase: FavorArtistUseCase

    @Inject
    lateinit var disfavorArtistUseCase: DisfavorArtistUseCase

    private val _artist = MutableLiveData<Artist>()
    val artist: LiveData<Artist> = _artist

    private val _tracks = MutableStateFlow<PagingData<TrackCell>>(PagingData.empty())
    val tracks: StateFlow<PagingData<TrackCell>> = _tracks

    private val _albums = MutableStateFlow<PagingData<Album>>(PagingData.empty())
    val albums: StateFlow<PagingData<Album>> = _albums

    private val _searchLoadingStateLiveData = MutableLiveData<LoadingState>()
    val searchLoadingStateLiveData: LiveData<LoadingState> =
        _searchLoadingStateLiveData

    private val searchExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("ArtistViewModel", "Exception caught: $throwable")
        _searchLoadingStateLiveData.postValue(LoadingState.ERROR)
    }
    private val searchCoroutineScope =
        CoroutineScope(Dispatchers.IO + searchExceptionHandler)

    private var currentContentType = ContentType.TRACKS

    fun searchContent(id: String) {
        when (currentContentType) {
            ContentType.TRACKS -> getTracks(id)
            ContentType.ALBUMS -> getAlbums(id)
        }
    }

    fun setCurrentContentType(type: ContentType, id: String) {
        currentContentType = type
        searchContent(id)
    }

    fun getCurrentContentType(): ContentType = currentContentType

    fun getArtist(id: String) {
        _searchLoadingStateLiveData.postValue(LoadingState.LOADING)
        searchCoroutineScope.launch {
            val artistInfo = getArtistUseCase(id)
            artistInfo.is_favorite = isFavoriteUseCase(id)
            _artist.postValue(artistInfo)
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

    private fun getAlbums(id: String) {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 10,
                    enablePlaceholders = false,
                    initialLoadSize = 20,
                ),
                pagingSourceFactory = {
                    AlbumsPagingSource(
                        getAlbumsUseCase,
                        id,
                    )
                }
            ).flow.cachedIn(viewModelScope).collect {
                _albums.value = it
            }
        }
    }

    fun favorArtist(id: String) {
        searchCoroutineScope.launch {
            favorArtistUseCase(id)
        }
    }

    fun disfavorArtist(id: String) {
        searchCoroutineScope.launch {
            disfavorArtistUseCase(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchCoroutineScope.cancel()
    }
}