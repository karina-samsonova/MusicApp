package com.example.playlist.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.network.LoadingState
import com.example.playlist.domain.model.Playlist
import com.example.playlist.domain.usecases.DisfavorPlaylistUseCase
import com.example.playlist.domain.usecases.FavorPlaylistUseCase
import com.example.playlist.domain.usecases.GetPlaylistUseCase
import com.example.playlist.domain.usecases.IsFavoriteUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaylistViewModel : ViewModel() {

    @Inject
    lateinit var getPlaylistUseCase: GetPlaylistUseCase

    @Inject
    lateinit var isFavoriteUseCase: IsFavoriteUseCase

    @Inject
    lateinit var favorPlaylistUseCase: FavorPlaylistUseCase

    @Inject
    lateinit var disfavorPlaylistUseCase: DisfavorPlaylistUseCase

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _searchLoadingStateLiveData = MutableLiveData<LoadingState>()
    val searchLoadingStateLiveData: LiveData<LoadingState> =
        _searchLoadingStateLiveData

    private val searchExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("PlaylistViewModel", "Exception caught: $throwable")
        _searchLoadingStateLiveData.postValue(LoadingState.ERROR)
    }
    private val searchCoroutineScope =
        CoroutineScope(Dispatchers.IO + searchExceptionHandler)

    fun getPlaylist(id: String) {
        _searchLoadingStateLiveData.postValue(LoadingState.LOADING)
        searchCoroutineScope.launch {
            val playlistInfo = getPlaylistUseCase(id)
            playlistInfo[0].is_favorite = isFavoriteUseCase(id)
            _playlist.postValue(playlistInfo[0])
            _searchLoadingStateLiveData.postValue(LoadingState.SUCCESS)
        }
    }

    fun favorPlaylist(id: String) {
        searchCoroutineScope.launch {
            favorPlaylistUseCase(id)
        }
    }

    fun disfavorPlaylist(id: String) {
        searchCoroutineScope.launch {
            disfavorPlaylistUseCase(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchCoroutineScope.cancel()
    }
}