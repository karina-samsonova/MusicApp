package com.example.home.data.provider

internal class ClientIdProvider(private val clientId: String) {

    fun getClientId(): String {
        return clientId
    }
}