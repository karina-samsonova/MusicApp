package com.example.auth.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auth.domain.AuthState
import com.example.auth.domain.usecases.IsUserLoggedInUseCase
import com.example.auth.domain.usecases.LoginUseCase
import com.example.auth.domain.usecases.LogoutUseCase
import com.example.auth.domain.usecases.RegisterUseCase
import com.example.auth.domain.usecases.ResetPasswordUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel : ViewModel() {

    @Inject
    lateinit var isUserLoggedInUseCase: IsUserLoggedInUseCase

    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    @Inject
    lateinit var registerUseCase: RegisterUseCase

    @Inject
    lateinit var resetPasswordUseCase: ResetPasswordUseCase

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val authExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("AuthViewModel", "Exception caught: $throwable")
        _authState.value = AuthState.Loading
    }
    private val authCoroutineScope =
        CoroutineScope(Dispatchers.Main + authExceptionHandler)

    fun login(email: String, password: String) {
        authCoroutineScope.launch {
            val result = loginUseCase(email, password)
            result.onSuccess {
                _authState.value = AuthState.Success
            }
            result.onFailure {
                _authState.value = AuthState.Error(it.message)
            }
        }
    }

    fun register(email: String, password: String) {
        authCoroutineScope.launch {
            val result = registerUseCase(email, password)
            result.onSuccess {
                _authState.value = AuthState.Success
            }
            result.onFailure {
                _authState.value = AuthState.Error(it.message)
            }
        }
    }

    fun resetPassword(email: String) {
        authCoroutineScope.launch {
            val result = resetPasswordUseCase(email)
            result.onSuccess {
                _authState.value = AuthState.Success
            }
            result.onFailure {
                _authState.value = AuthState.Error(it.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        authCoroutineScope.cancel()
    }
}