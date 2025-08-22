package com.example.home.data.mapper

import com.example.home.domain.model.TrackCell
import javax.inject.Inject
import com.example.exoplayer.domain.model.TrackCell as ExoTrack

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