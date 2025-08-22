package com.example.settings.presentation.viewModel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.example.settings.domain.model.ThemeMode
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SettingsViewModel : ViewModel() {

    fun setTheme(themeMode: ThemeMode) {
        when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ThemeMode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ThemeMode.SYSTEM ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun getCurrentTheme(): ThemeMode {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> ThemeMode.LIGHT
            AppCompatDelegate.MODE_NIGHT_YES -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    fun logout() {
        Firebase.auth.signOut()
    }
}