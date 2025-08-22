package com.example.home.domain

import com.example.home.domain.model.TrackCell

interface HomeRepository {

    suspend fun getNewTracks(): List<TrackCell>
}