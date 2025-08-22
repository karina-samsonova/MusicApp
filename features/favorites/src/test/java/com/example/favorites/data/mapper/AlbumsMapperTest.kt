package com.example.favorites.data.mapper

import com.example.network.model.AlbumDto
import com.example.network.model.AlbumResponseDto
import com.example.network.model.AlbumTrackDto
import com.example.network.model.HeaderDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlbumsMapperTest {

    private val mapper = AlbumsMapper()

    private val testHeader = HeaderDto(
        status = "success",
        code = 0,
        error_message = "",
        warnings = "",
        results_count = 1,
        next = null
    )

    private val testAlbumTrackDto =
        AlbumTrackDto(
            id = "1",
            position = "0",
            name = "Test Track",
            duration = "180",
            license_ccurl = "",
            audio = "audio.mp3",
            audiodownload = "download.mp3",
            audiodownload_allowed = true
        )

    private val testAlbumDto = AlbumDto(
        id = "123",
        name = "Test Album",
        releasedate = "2020-01-01",
        artist_id = "2345",
        artist_name = "Test Artist",
        image = "image.jpg",
        zip = "",
        shorturl = "short",
        shareurl = "share",
        zip_allowed = false,
        tracks = listOf(testAlbumTrackDto)
    )

    private val testAlbumResponseDto = AlbumResponseDto(
        headers = testHeader.copy(next = "next_page_url"),
        results = listOf(testAlbumDto)
    )

    @Test
    fun `mapDtoToEntity converts AlbumDto correctly`() {
        // Act
        val result = mapper.mapDtoToEntity(testAlbumDto)

        // Assert
        assertEquals("123", result.id)
        assertEquals("Test Album", result.name)
        assertEquals("2020-01-01", result.releasedate)
        assertEquals("Test Artist", result.artist_name)
        assertEquals("image.jpg", result.image)
        assertEquals(1, result.tracks.size)
    }

    @Test
    fun `mapListDtoToEntity converts AlbumResponseDto correctly`() {
        // Act
        val result = mapper.mapListDtoToEntity(testAlbumResponseDto)

        // Assert
        assertEquals("next_page_url", result.next)
        assertEquals(1, result.content.size)
        assertEquals("Test Album", result.content[0].name)
    }

    @Test
    fun `mapDtoToEntity converts AlbumTrackDto correctly`() {
        // Arrange
        val trackDto = testAlbumDto.tracks!![0]
        val image = "cover.jpg"
        val artistName = "Artist"

        // Act
        val result = mapper.mapDtoToEntity(trackDto, image, artistName)

        // Assert
        assertEquals("1", result.id)
        assertEquals(0, result.position)
        assertEquals("Test Track", result.name)
        assertEquals(artistName, result.artist_name)
        assertEquals(image, result.image)
        assertEquals(180, result.duration)
        assertTrue(result.audiodownload_allowed)
    }

    @Test
    fun `mapListDtoToEntity handles empty tracks list`() {
        // Arrange
        val dto = testAlbumDto.copy(tracks = emptyList())

        // Act
        val result = mapper.mapDtoToEntity(dto)

        // Assert
        assertTrue(result.tracks.isEmpty())
    }

    @Test
    fun `mapDtoToEntity handles null tracks`() {
        // Arrange
        val dto = testAlbumDto.copy(tracks = null)

        // Act
        val result = mapper.mapDtoToEntity(dto)

        // Assert
        assertTrue(result.tracks.isEmpty())
    }

    @Test
    fun `mapDtoToEntity handles invalid position`() {
        // Arrange
        val trackDto = testAlbumTrackDto.copy(position = "invalid")

        // Act
        val result = mapper.mapDtoToEntity(trackDto, "image.jpg", "Artist")

        // Assert
        assertEquals(0, result.position)
    }

    @Test
    fun `mapDtoToEntity handles invalid duration`() {
        // Arrange
        val trackDto = testAlbumTrackDto.copy(duration = "invalid")

        // Act
        val result = mapper.mapDtoToEntity(trackDto, "image.jpg", "Artist")

        // Assert
        assertEquals(0, result.duration)
    }
}