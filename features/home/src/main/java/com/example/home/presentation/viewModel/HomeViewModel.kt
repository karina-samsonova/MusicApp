package com.example.home.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.home.domain.model.TrackCell
import com.example.home.domain.usecases.GetNewTracksUseCase
import com.example.network.LoadingState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel : ViewModel() {

    @Inject
    lateinit var getNewTracksUseCase: GetNewTracksUseCase

    private val _tracks = MutableLiveData<List<TrackCell>>()
    val tracks: LiveData<List<TrackCell>> = _tracks

    private val _searchLoadingStateLiveData = MutableLiveData<LoadingState>()
    val searchLoadingStateLiveData: LiveData<LoadingState> =
        _searchLoadingStateLiveData

    private val searchExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("PlaylistViewModel", "Exception caught: $throwable")
        _searchLoadingStateLiveData.postValue(LoadingState.ERROR)
    }
    private val searchCoroutineScope =
        CoroutineScope(Dispatchers.IO + searchExceptionHandler)

    fun getNewTracks() {
        _searchLoadingStateLiveData.postValue(LoadingState.LOADING)
        searchCoroutineScope.launch {
            val trackList = getNewTracksUseCase()
            _tracks.postValue(trackList)
            _searchLoadingStateLiveData.postValue(LoadingState.SUCCESS)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchCoroutineScope.cancel()
    }
}