package com.example.exoplayer.presentation.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.ShuffleOrder
import com.example.exoplayer.domain.model.TrackCell
import com.example.exoplayer.domain.usecases.DisfavorTrackUseCase
import com.example.exoplayer.domain.usecases.FavorTrackUseCase
import com.example.exoplayer.domain.usecases.GetTracksUseCase
import com.example.exoplayer.domain.usecases.IsFavoriteUseCase
import com.example.exoplayer.service.PlaybackService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    val getTracksUseCase: GetTracksUseCase,
    val isFavoriteUseCase: IsFavoriteUseCase,
    val favorTrackUseCase: FavorTrackUseCase,
    val disfavorTrackUseCase: DisfavorTrackUseCase,
    val context: Application
) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private var playbackService: PlaybackService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlaybackService.PlaybackBinder
            playbackService = binder.getService()
            isBound = true

            playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
                val mediaItems = createMediaItems(currentPlaylist)
                exoPlayer.setMediaItems(mediaItems)
                playTrack(savedPosition)

                _playlistUpdated.postValue(Unit)

                setupPlayerListeners()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playbackService = null
            isBound = false
        }
    }

    private fun startServiceAndBind() {
        val intent = Intent(context, PlaybackService::class.java)
        ContextCompat.startForegroundService(context, intent)
        context.bindService(
            intent, serviceConnection, Context.BIND_AUTO_CREATE
        )
    }

    private val _currentTrack = MutableLiveData<TrackCell?>(null)
    val currentTrack: LiveData<TrackCell?> = _currentTrack

    private val _playlistUpdated = MutableLiveData<Unit>()
    val playlistUpdated: LiveData<Unit> = _playlistUpdated

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    var currentPlaylist: MutableList<TrackCell> = mutableListOf()
    private var currentId: String? = null
    private var currentNamesearch: String? = null
    private var currentTags: String = ""
    private var currentArtistId: String = ""
    private var currentAlbumId: String = ""
    private var currentOrder: String = ""
    var savedPosition: Int = 0

    private val playerExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("PlayerViewModel", "Exception caught: $throwable")
    }
    private val playerCoroutineScope = CoroutineScope(Dispatchers.Main + playerExceptionHandler)

    private fun setupPlayerListeners() {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    if (currentPlaylist.isNotEmpty() && _currentTrack.value != getCurrentTrack()) {
                        playerCoroutineScope.launch {
                            val track = getCurrentTrack()
                            track?.let {
                                it.is_favorite = isFavoriteUseCase(it.id)
                                playbackService!!.updateNotification(it)
                            }
                            _currentTrack.value = track
                            if (currentPlaylist.size - exoPlayer.currentMediaItemIndex <= 1) loadPlaylist()
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e("PlayerViewModel", "Playback error", error)
                }
            })
        }
    }

    fun setPlaylist(
        id: String,
        artistId: String,
        albumId: String,
        namesearch: String,
        tags: String,
        position: Int,
        order: String
    ) {
        if (id == currentId && namesearch == currentNamesearch && order == currentOrder
            && artistId == currentArtistId && albumId == currentAlbumId && tags == currentTags
            && currentPlaylist.size != 0 && position < currentPlaylist.size
        ) {
            playTrack(position)
        }

        if (!isBound) {
            playerCoroutineScope.launch {
                getPlaylistReady(id, artistId, albumId, namesearch, tags, position, order)

                savedPosition = position

                startServiceAndBind()
            }
        } else {
            playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
                playerCoroutineScope.launch {
                    getPlaylistReady(id, artistId, albumId, namesearch, tags, position, order)

                    val mediaItems = createMediaItems(currentPlaylist)
                    exoPlayer.setMediaItems(mediaItems)
                    playTrack(position)

                    _playlistUpdated.postValue(Unit)
                }
                setupPlayerListeners()
            }
        }
    }

    private suspend fun getPlaylistReady(
        id: String,
        artistId: String,
        albumId: String,
        namesearch: String,
        tags: String,
        position: Int,
        order: String
    ) {
        var tracks =
            getTracksUseCase(id, artistId, albumId, namesearch, tags, position + 10, 0, order)

        if (id != "") {
            val ids = id.split("+")
            tracks = tracks.sortedWith(compareByDescending { ids.indexOf(it.id) })
        }

        currentPlaylist = tracks.toMutableList()
        currentId = id
        currentNamesearch = namesearch
        currentOrder = order
        currentArtistId = artistId
        currentAlbumId = albumId
        currentTags = tags
        savedPosition = position
    }

    fun loadPlaylist() {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            if (currentId != null && currentNamesearch != null) {
                playerCoroutineScope.launch {
                    val tracks = getTracksUseCase(
                        currentId!!,
                        currentArtistId,
                        currentAlbumId,
                        currentNamesearch!!,
                        currentTags,
                        10,
                        currentPlaylist.size,
                        currentOrder
                    )
                    val mediaItems = createMediaItems(tracks)
                    exoPlayer.addMediaItems(mediaItems)
                    currentPlaylist.addAll(tracks.toMutableList())
                }
            }
        }
    }

    fun setPlaylist(id: String, namesearch: String, tracks: List<TrackCell>, position: Int) {
        if (tracks == currentPlaylist) {
            playTrack(position)
        }

        if (!isBound) {
            currentPlaylist = tracks.toMutableList()
            currentId = id
            currentNamesearch = namesearch
            savedPosition = position
            startServiceAndBind()
        } else {
            playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
                currentPlaylist = tracks.toMutableList()
                currentId = id
                currentNamesearch = namesearch

                val mediaItems = createMediaItems(tracks)
                exoPlayer.setMediaItems(mediaItems)
                playTrack(position)

                _playlistUpdated.postValue(Unit)

                setupPlayerListeners()
            }
        }
    }

    fun setPlaylist(tracks: List<TrackCell>, position: Int) {
        if (tracks == currentPlaylist) {
            playTrack(position)
        }

        if (!isBound) {
            currentPlaylist = tracks.toMutableList()
            currentId = null
            currentNamesearch = null
            savedPosition = position
            startServiceAndBind()
        } else {
            playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
                currentPlaylist = tracks.toMutableList()
                currentId = null
                currentNamesearch = null

                val mediaItems = createMediaItems(tracks)
                exoPlayer.setMediaItems(mediaItems)
                playTrack(position)

                _playlistUpdated.postValue(Unit)

                setupPlayerListeners()
            }
        }
    }

    private fun favorTrack(id: String) {
        playerCoroutineScope.launch {
            favorTrackUseCase(id)
        }
    }

    private fun disfavorTrack(id: String) {
        playerCoroutineScope.launch {
            disfavorTrackUseCase(id)
        }
    }

    private fun playTrack(position: Int) {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            playerCoroutineScope.launch {
                exoPlayer.seekTo(position, 0L)
                exoPlayer.prepare()
                exoPlayer.play()
                val track = getCurrentTrack()
                track?.let { it.is_favorite = isFavoriteUseCase(it.id) }
                _currentTrack.value = track
            }
        }
    }

    fun nextTrack() {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            exoPlayer.seekToNext()
            _currentTrack.value = getCurrentTrack()
        }
    }

    fun previousTrack() {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            exoPlayer.seekToPrevious()
            _currentTrack.value = getCurrentTrack()
        }
    }

    fun seekTo(position: Long) {
        playbackService?.PlaybackBinder()?.getPlayer()?.seekTo(position)
    }

    fun duration(): Int = playbackService?.PlaybackBinder()?.getPlayer()?.duration!!.toInt()

    fun currentPosition(): Int =
        playbackService?.PlaybackBinder()?.getPlayer()?.currentPosition!!.toInt()

    fun playOrPauseTrack() {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            if (exoPlayer.isPlaying) exoPlayer.pause()
            else exoPlayer.play()
        }
    }

    @OptIn(UnstableApi::class)
    fun changeShuffleMode(): Boolean {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            exoPlayer.shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
            if (exoPlayer.shuffleModeEnabled) exoPlayer.setShuffleOrder(
                ShuffleOrder.DefaultShuffleOrder(
                    exoPlayer.mediaItemCount
                )
            )
            return exoPlayer.shuffleModeEnabled
        }
        return false
    }

    @OptIn(UnstableApi::class)
    fun getShuffleMode(): Boolean {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            return exoPlayer.shuffleModeEnabled
        }
        return false
    }

    fun changeRepeatMode(): Int {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            when (exoPlayer.repeatMode) {
                Player.REPEAT_MODE_OFF -> exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ONE -> exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
            }
            return exoPlayer.repeatMode
        }
        return Player.REPEAT_MODE_OFF
    }

    fun getRepeatMode(): Int {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            return exoPlayer.repeatMode
        }
        return Player.REPEAT_MODE_OFF
    }

    fun favorOrDisfavorTrack(track: TrackCell): Boolean {
        if (track.is_favorite) {
            disfavorTrack(track.id)
            return false
        } else {
            favorTrack(track.id)
            return true
        }
    }

    private fun getCurrentTrack(): TrackCell? {
        playbackService?.PlaybackBinder()?.getPlayer()?.let { exoPlayer ->
            return currentPlaylist[exoPlayer.currentMediaItemIndex]
        }
        return null
    }

    private fun createMediaItems(tracks: List<TrackCell>): List<MediaItem> =
        tracks.map { MediaItem.fromUri(it.audio) }

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            context.unbindService(serviceConnection)
        }
        playerCoroutineScope.cancel()
    }
}