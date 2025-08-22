package com.example.playlist.data.mapper

import com.example.playlist.domain.model.TrackCell
import com.example.exoplayer.domain.model.TrackCell as ExoTrack
import javax.inject.Inject

internal class ExoPlayerTrackMapper @Inject constructor() {

    private fun mapDtoToEntity(track: TrackCell) = ExoTrack(
        id = track.id,
        position = track.position,
        image = track.image,
        name = track.name,
        artist_name = track.artist_name,
        duration = track.duration,
        audio = track.audio,
        audiodownload = track.audiodownload,
        audiodownload_allowed = track.audiodownload_allowed,
    )

    fun mapListDtoToExoEntity(tracks: List<TrackCell>): List<ExoTrack> =
        tracks.map { mapDtoToEntity(it) }

}