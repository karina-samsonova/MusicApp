package com.example.network.di

import com.example.network.ApiHelper
import dagger.Component

@Component(modules = [NetworkModule::class])
interface NetworkComponent {
    fun apiHelper(): ApiHelper
}