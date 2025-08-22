package com.example.playlist.data.provider

internal class ClientIdProvider(private val clientId: String) {

    fun getClientId(): String {
        return clientId
    }
}