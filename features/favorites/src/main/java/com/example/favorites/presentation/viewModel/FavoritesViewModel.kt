package com.example.favorites.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.favorites.domain.model.Album
import com.example.favorites.domain.model.Artist
import com.example.favorites.domain.model.ContentType
import com.example.favorites.domain.model.Playlist
import com.example.favorites.domain.model.TrackCell
import com.example.favorites.domain.paging.AlbumsPagingSource
import com.example.favorites.domain.paging.ArtistsPagingSource
import com.example.favorites.domain.paging.PlaylistsPagingSource
import com.example.favorites.domain.paging.TracksPagingSource
import com.example.favorites.domain.usecases.GetAlbumsUseCase
import com.example.favorites.domain.usecases.GetArtistsUseCase
import com.example.favorites.domain.usecases.GetFavoriteAlbumsUseCase
import com.example.favorites.domain.usecases.GetFavoriteArtistsUseCase
import com.example.favorites.domain.usecases.GetFavoritePlaylistsUseCase
import com.example.favorites.domain.usecases.GetFavoriteTracksUseCase
import com.example.favorites.domain.usecases.GetPlaylistsUseCase
import com.example.favorites.domain.usecases.GetTracksUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesViewModel : ViewModel() {

    @Inject
    lateinit var getTracksUseCase: GetTracksUseCase

    @Inject
    lateinit var getArtistsUseCase: GetArtistsUseCase

    @Inject
    lateinit var getAlbumsUseCase: GetAlbumsUseCase

    @Inject
    lateinit var getPlaylistsUseCase: GetPlaylistsUseCase

    @Inject
    lateinit var getFavoriteTracksUseCase: GetFavoriteTracksUseCase

    @Inject
    lateinit var getFavoriteAlbumsUseCase: GetFavoriteAlbumsUseCase

    @Inject
    lateinit var getFavoriteArtistsUseCase: GetFavoriteArtistsUseCase

    @Inject
    lateinit var getFavoritePlaylistsUseCase: GetFavoritePlaylistsUseCase


    private val _tracks = MutableStateFlow<PagingData<TrackCell>>(
        PagingData.empty()
    )
    val tracks: StateFlow<PagingData<TrackCell>> = _tracks

    private val _artists = MutableStateFlow<PagingData<Artist>>(
        PagingData.empty()
    )
    val artists: StateFlow<PagingData<Artist>> = _artists

    private val _albums = MutableStateFlow<PagingData<Album>>(
        PagingData.empty()
    )
    val albums: StateFlow<PagingData<Album>> = _albums

    private val _playlists = MutableStateFlow<PagingData<Playlist>>(
        PagingData.empty()
    )
    val playlists: StateFlow<PagingData<Playlist>> = _playlists


    private var currentContentType = ContentType.TRACKS
    private var currentAutocomplete = ""
    private var trackIds = ""

    private val initialLoadSize = 40
    private val pageSize = 30
    private val prefetchDistance = 20

    private val debouncePeriod: Long = 500
    private var searchJob: Job = Job()

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

    fun getTrackIds(): String = trackIds

    fun getCurrentAutocomplete() = currentAutocomplete

    fun onSearchQuery(query: String?) {
        searchJob.cancel()
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
            setCurrentAutocomplete(query ?: "")
        }
    }

    private fun getTracks() {
        viewModelScope.launch {
            trackIds = getFavoriteTracksUseCase().joinToString("+")
            _tracks.value = PagingData.empty()
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
                        trackIds,
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
                        getFavoriteArtistsUseCase,
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
                        getFavoriteAlbumsUseCase,
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
                        getFavoritePlaylistsUseCase,
                        currentAutocomplete
                    )
                }
            ).flow.cachedIn(viewModelScope).collect {
                _playlists.value = it
            }
        }
    }
}