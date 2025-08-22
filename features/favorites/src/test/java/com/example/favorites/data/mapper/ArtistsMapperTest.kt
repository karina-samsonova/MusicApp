package com.example.favorites.data.mapper

import com.example.network.model.ArtistDto
import com.example.network.model.ArtistResponseDto
import com.example.network.model.ArtistTrackDto
import com.example.network.model.HeaderDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArtistsMapperTest {

    private val mapper = ArtistsMapper()

    private val testHeader = HeaderDto(
        status = "success",
        code = 0,
        error_message = "",
        warnings = "",
        results_count = 1,
        next = null
    )

    private val testArtistTrackDto = ArtistTrackDto(
        album_id = "321",
        album_name = "Test Album",
        id = "1",
        name = "Test Track",
        duration = "180",
        releasedate = "2020-01-01",
        license_ccurl = "",
        album_image = "album_image.jpg",
        image = "image.jpg",
        audio = "audio.mp3",
        audiodownload = "download.mp3",
        audiodownload_allowed = true
    )

    private val testArtistDto = ArtistDto(
        id = "123",
        name = "Test Artist",
        website = "https://example.com",
        joindate = "2020-01-01",
        image = "image.jpg",
        shorturl = "short",
        shareurl = "share",
        tracks = listOf(testArtistTrackDto)
    )

    private val testArtistResponseDto = ArtistResponseDto(
        headers = testHeader.copy(next = "next_page_url"),
        results = listOf(testArtistDto)
    )

    @Test
    fun `mapDtoToEntity for ArtistDto converts correctly`() {
        // Act
        val result = mapper.mapDtoToEntity(testArtistDto)

        // Assert
        assertEquals("123", result.id)
        assertEquals("Test Artist", result.name)
        assertEquals("https://example.com", result.website)
        assertEquals(1, result.tracks.size)
    }

    @Test
    fun `mapListDtoToEntity for ArtistResponseDto converts correctly`() {
        // Act
        val result = mapper.mapListDtoToEntity(testArtistResponseDto)

        // Assert
        assertEquals("next_page_url", result.next)
        assertEquals(1, result.content.size)
        assertEquals("Test Artist", result.content[0].name)
    }

    @Test
    fun `mapDtoToEntity for ArtistTrackDto converts correctly`() {
        // Arrange
        val trackDto = testArtistDto.tracks!![0]
        val artistName = "Artist"

        // Act
        val result = mapper.mapListDtoToEntity(listOf(trackDto), artistName)[0]

        // Assert
        assertEquals("1", result.id)
        assertEquals("Test Track", result.name)
        assertEquals(artistName, result.artist_name)
        assertEquals(180, result.duration)
        assertTrue(result.audiodownload_allowed)
    }

    @Test
    fun `mapListDtoToEntity handles empty tracks list`() {
        // Arrange
        val dto = testArtistDto.copy(tracks = emptyList())

        // Act
        val result = mapper.mapListDtoToEntity(
            ArtistResponseDto(
                headers = testHeader,
                results = listOf(dto)
            )
        )

        // Assert
        assertNull(result.next)
        assertTrue(result.content[0].tracks.isEmpty())
    }

    @Test
    fun `mapDtoToEntity handles null tracks`() {
        // Arrange
        val dto = testArtistDto.copy(tracks = null)

        // Act
        val result = mapper.mapListDtoToEntity(
            ArtistResponseDto(
                headers = testHeader,
                results = listOf(dto)
            )
        ).content[0]

        // Assert
        assertTrue(result.tracks.isEmpty())
    }

    @Test
    fun `mapDtoToEntity for TrackDto converts duration to Int`() {
        // Arrange
        val trackDto = testArtistTrackDto.copy(duration = "invalid")

        // Act
        val result = mapper.mapListDtoToEntity(listOf(trackDto), "Artist")[0]

        // Assert
        assertEquals(0, result.duration)
    }
}