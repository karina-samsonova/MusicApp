package com.example.exoplayer.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.media3.session.legacy.MediaButtonReceiver
import androidx.media3.session.legacy.PlaybackStateCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.exoplayer.domain.model.TrackCell
import com.example.exoplayer.receivers.HeadsetReceiver

const val NOTIFICATION_ID = 1001
const val CHANNEL_ID = "music_playback_channel"

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private var binder: PlaybackBinder? = null
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var headsetReceiver: HeadsetReceiver

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, exoPlayer).build()
        binder = PlaybackBinder()

        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        headsetReceiver = HeadsetReceiver {
            exoPlayer.pause()
        }

        registerReceiver(
            headsetReceiver,
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        )
    }

    @SuppressLint("RestrictedApi")
    @OptIn(UnstableApi::class)
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(androidx.media3.session.R.drawable.media3_notification_small_icon)
            .addAction(
                NotificationCompat.Action(
                    androidx.media3.session.R.drawable.media3_icon_previous,
                    "Previous",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                )
            )
            .addAction(
                NotificationCompat.Action(
                    if (exoPlayer.isPlaying) androidx.media3.session.R.drawable.media3_icon_pause
                    else androidx.media3.session.R.drawable.media3_icon_play,
                    if (exoPlayer.isPlaying) "Pause" else "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                )
            )
            .addAction(
                NotificationCompat.Action(
                    androidx.media3.session.R.drawable.media3_icon_next,
                    "Next",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                )
            )
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession!!)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            .build()
    }

    @SuppressLint("RestrictedApi")
    @OptIn(UnstableApi::class)
    fun updateNotification(track: TrackCell) {
        Glide.with(this)
            .asBitmap()
            .load(track.image)
            .override(512, 512)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    updateWithBitmap(track, bitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    @SuppressLint("RestrictedApi")
    @OptIn(UnstableApi::class)
    fun updateWithBitmap(track: TrackCell, bitmap: Bitmap) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setLargeIcon(bitmap)
            .setContentTitle(track.name)
            .setContentText(track.artist_name)
            .setSmallIcon(androidx.media3.session.R.drawable.media3_notification_small_icon)
            .addAction(
                NotificationCompat.Action(
                    androidx.media3.session.R.drawable.media3_icon_previous,
                    "Previous",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                )
            )
            .addAction(
                NotificationCompat.Action(
                    if (exoPlayer.isPlaying) androidx.media3.session.R.drawable.media3_icon_pause
                    else androidx.media3.session.R.drawable.media3_icon_play,
                    if (exoPlayer.isPlaying) "Pause" else "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                )
            )
            .addAction(
                NotificationCompat.Action(
                    androidx.media3.session.R.drawable.media3_icon_next,
                    "Next",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                )
            )
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession!!)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Music Playback",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Music player controls"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return binder
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            unregisterReceiver(headsetReceiver)
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    inner class PlaybackBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
        fun getPlayer(): ExoPlayer = exoPlayer
    }
}