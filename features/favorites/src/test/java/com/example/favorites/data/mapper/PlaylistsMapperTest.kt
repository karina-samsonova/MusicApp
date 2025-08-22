package com.example.favorites.data.mapper

import com.example.network.model.PlaylistDto
import com.example.network.model.PlaylistResponseDto
import com.example.network.model.PlaylistTrackDto
import com.example.network.model.HeaderDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaylistsMapperTest {

    private val mapper = PlaylistsMapper()

    private val testHeader = HeaderDto(
        status = "success",
        code = 0,
        error_message = "",
        warnings = "",
        results_count = 1,
        next = null
    )

    private val testPlaylistTrackDto =
        PlaylistTrackDto(
            id = "1",
            name = "Test Track",
            album_id = "123",
            artist_id = "321",
            duration = "180",
            artist_name = "Test Artist",
            playlistadddate = "2020-01-01",
            position = "0",
            license_ccurl = "",
            album_image = "album_image.jpg",
            image = "image.jpg",
            audio = "audio.mp3",
            audiodownload = "download.mp3",
            audiodownload_allowed = true
        )

    private val testPlaylistDto = PlaylistDto(
        id = "123",
        name = "Test Playlist",
        creationdate = "2020-01-01",
        user_id = "2345",
        user_name = "Test Artist",
        zip = "",
        shorturl = "short",
        shareurl = "share",
        tracks = listOf(testPlaylistTrackDto)
    )

    private val testPlaylistResponseDto = PlaylistResponseDto(
        headers = testHeader.copy(next = "next_page_url"),
        results = listOf(testPlaylistDto)
    )

    @Test
    fun `mapDtoToEntity for PlaylistDto converts correctly`() {
        // Act
        val result = mapper.mapDtoToEntity(testPlaylistDto)

        // Assert
        assertEquals("123", result.id)
        assertEquals("Test Playlist", result.name)
        assertEquals("Test Artist", result.user_name)
        assertEquals(1, result.tracks.size)
        assertEquals("Test Track", result.tracks[0].name)
    }

    @Test
    fun `mapListDtoToEntity for PlaylistResponseDto converts correctly`() {
        // Act
        val result = mapper.mapListDtoToEntity(testPlaylistResponseDto)

        // Assert
        assertEquals("next_page_url", result.next)
        assertEquals(1, result.content.size)
        assertEquals("Test Playlist", result.content[0].name)
        assertEquals("Test Track", result.content[0].tracks[0].name)
    }

    @Test
    fun `mapDtoToEntity for PlaylistTrackDto converts correctly`() {
        // Act
        val result = mapper.mapDtoToEntity(testPlaylistTrackDto)

        // Assert
        assertEquals("1", result.id)
        assertEquals("Test Track", result.name)
        assertEquals("Test Artist", result.artist_name)
        assertEquals(180, result.duration)
        assertEquals(0, result.position)
        assertTrue(result.audiodownload_allowed)
    }

    @Test
    fun `mapListDtoToEntity handles empty tracks list`() {
        // Arrange
        val dto = testPlaylistDto.copy(tracks = emptyList())

        // Act
        val result = mapper.mapDtoToEntity(dto)

        // Assert
        assertTrue(result.tracks.isEmpty())
    }

    @Test
    fun `mapDtoToEntity handles null tracks`() {
        // Arrange
        val dto = testPlaylistDto.copy(tracks = null)

        // Act
        val result = mapper.mapDtoToEntity(dto)

        // Assert
        assertTrue(result.tracks.isEmpty())
    }

    @Test
    fun `mapDtoToEntity for TrackDto handles invalid duration`() {
        // Arrange
        val trackDto = testPlaylistTrackDto.copy(duration = "invalid")

        // Act
        val result = mapper.mapDtoToEntity(trackDto)

        // Assert
        assertEquals(0, result.duration)
    }

    @Test
    fun `mapDtoToEntity for TrackDto handles invalid position`() {
        // Arrange
        val trackDto = testPlaylistTrackDto.copy(position = "invalid")

        // Act
        val result = mapper.mapDtoToEntity(trackDto)

        // Assert
        assertEquals(0, result.position)
    }
}