package com.example.musicapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.di.App
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var playerViewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (savedInstanceState == null)
            checkAuthState()
    }

    private fun checkAuthState() {
        val destination = if (Firebase.auth.currentUser != null) {
            R.id.action_splash_to_main
        } else {
            R.id.action_splash_to_auth
        }
        navController.navigate(destination)
    }

}