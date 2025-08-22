package com.example.search.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.network.LoadingState
import com.example.search.domain.model.Album
import com.example.search.domain.model.Artist
import com.example.search.domain.model.ContentType
import com.example.search.domain.model.Playlist
import com.example.search.domain.model.TrackCell
import com.example.search.domain.usecases.GetTracksUseCase
import com.example.search.domain.paging.AlbumsPagingSource
import com.example.search.domain.paging.ArtistsPagingSource
import com.example.search.domain.paging.PlaylistsPagingSource
import com.example.search.domain.paging.TracksPagingSource
import com.example.search.domain.usecases.GetAlbumsUseCase
import com.example.search.domain.usecases.GetArtistsUseCase
import com.example.search.domain.usecases.GetAutocompleteUseCase
import com.example.search.domain.usecases.GetPlaylistsUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel : ViewModel() {

    @Inject
    lateinit var getAutocompleteUseCase: GetAutocompleteUseCase

    @Inject
    lateinit var getTracksUseCase: GetTracksUseCase

    @Inject
    lateinit var getArtistsUseCase: GetArtistsUseCase

    @Inject
    lateinit var getAlbumsUseCase: GetAlbumsUseCase

    @Inject
    lateinit var getPlaylistsUseCase: GetPlaylistsUseCase

    private val _autocompletes = MutableLiveData<List<String>>()
    val autocompletes: LiveData<List<String>> = _autocompletes

    private val _tracks = MutableStateFlow<PagingData<TrackCell>>(PagingData.empty())
    val tracks: StateFlow<PagingData<TrackCell>> = _tracks

    private val _artists = MutableStateFlow<PagingData<Artist>>(PagingData.empty())
    val artists: StateFlow<PagingData<Artist>> = _artists

    private val _albums = MutableStateFlow<PagingData<Album>>(PagingData.empty())
    val albums: StateFlow<PagingData<Album>> = _albums

    private val _playlists = MutableStateFlow<PagingData<Playlist>>(PagingData.empty())
    val playlists: StateFlow<PagingData<Playlist>> = _playlists


    private val _autocompleteLoadingStateLiveData = MutableLiveData<LoadingState>()
    val autocompleteLoadingStateLiveData: LiveData<LoadingState> = _autocompleteLoadingStateLiveData

    private val autocompleteExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("SearchViewModel", "Exception caught: $throwable")
        _autocompleteLoadingStateLiveData.postValue(LoadingState.ERROR)
    }
    private val autocompleteCoroutineScope =
        CoroutineScope(Dispatchers.IO + autocompleteExceptionHandler)


    private var currentContentType = ContentType.TRACKS
    private var currentAutocomplete = ""

    private val debouncePeriod: Long = 500
    private var searchJob: Job = Job()

    private val initialLoadSize = 40
    private val pageSize = 30
    private val prefetchDistance = 20

    fun searchContent() {
        when (currentContentType) {
            ContentType.TRACKS -> getTracks()
            ContentType.ARTISTS -> getArtists()
            ContentType.ALBUMS -> getAlbums()
            ContentType.PLAYLISTS -> getPlaylists()
        }
    }

    fun setCurrentContentType(type: ContentType) {
        currentContentType = type
        searchContent()
    }

    fun getCurrentContentType(): ContentType = currentContentType

    fun setCurrentAutocomplete(autocomplete: String) {
        currentAutocomplete = autocomplete
        searchContent()
    }

    fun getCurrentAutocomplete() = currentAutocomplete

    fun onSearchQuery(query: String?) {
        searchJob.cancel()
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
            if (query.toString().length >= 2)
                getAutocomplete(query)
            else if (query.isNullOrEmpty())
                setCurrentAutocomplete("")
        }
    }

    private fun getAutocomplete(query: String?) {
        _autocompleteLoadingStateLiveData.postValue(LoadingState.LOADING)
        autocompleteCoroutineScope.launch {
            if (query.isNullOrEmpty()) {
                _autocompletes.postValue(emptyList())
            } else {
                var matches = getAutocompleteUseCase(query, currentContentType)
                if (matches.size > 6)
                    matches = matches.subList(0, 6)
                _autocompletes.postValue(matches)
            }
            _autocompleteLoadingStateLiveData.postValue(LoadingState.SUCCESS)
        }
    }

    private fun getTracks() {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    prefetchDistance = prefetchDistance,
                    enablePlaceholders = false,
                    initialLoadSize = initialLoadSize,
                ),
                pagingSourceFactory = {
                    TracksPagingSource(
                        getTracksUseCase,
                        "",
                        currentAutocomplete
                    )
                }
            ).flow.cachedIn(viewModelScope).collect {
                _tracks.value = it
            }
        }
    }

    private fun getArtists() {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    prefetchDistance = prefetchDistance,
                    enablePlaceholders = false,
                    initialLoadSize = initialLoadSize,
                ),
                pagingSourceFactory = {
                    ArtistsPagingSource(
                        getArtistsUseCase,
                        "",
                        currentAutocomplete
                    )
                }
            ).flow.cachedIn(viewModelScope).collect {
                _artists.value = it
            }
        }
    }

    private fun getAlbums() {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    prefetchDistance = prefetchDistance,
                    enablePlaceholders = false,
                    initialLoadSize = initialLoadSize,
                ),
                pagingSourceFactory = {
                    AlbumsPagingSource(
                        getAlbumsUseCase,
                        "",
                        currentAutocomplete
                    )
                }
            ).flow.cachedIn(viewModelScope).collect {
                _albums.value = it
            }
        }
    }

    private fun getPlaylists() {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    prefetchDistance = prefetchDistance,
                    enablePlaceholders = false,
                    initialLoadSize = initialLoadSize,
                ),
                pagingSourceFactory = {
                    PlaylistsPagingSource(
                        getPlaylistsUseCase,
                        "",
                        currentAutocomplete
                    )
                }
            ).flow.cachedIn(viewModelScope).collect {
                _playlists.value = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autocompleteCoroutineScope.cancel()
    }
}