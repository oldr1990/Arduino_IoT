package com.github.oldr1990.ui.auth


import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.oldr1990.data.Constants.LOG_TAG
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.repository.RepositoryInterface
import com.github.oldr1990.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AuthViewModel @ViewModelInject constructor(
    private val repository: RepositoryInterface
) : ViewModel() {
    sealed class AuthEvent {
        class Success(val userID: String) : AuthEvent()
        class Error(val message: String) : AuthEvent()
        object WrongEmail : AuthEvent()
        object WrongPassword : AuthEvent()
        object Empty : AuthEvent()
    }

    var isEventHandled = true
    private val _savedEmail = MutableStateFlow("")
    private val _savedPassword = MutableStateFlow("")
    val savedEmail: StateFlow<String> = _savedEmail
    val savedPassword: StateFlow<String> = _savedPassword

    fun onEmailChanged(email: String) {
        _savedEmail.value = email
    }

    fun onPasswordChanged(password: String) {
        _savedPassword.value = password
    }

    private val _authEvent = MutableStateFlow<AuthEvent>(AuthEvent.Empty)
    val authEvent: StateFlow<AuthEvent> = _authEvent

    init {
        viewModelScope.launch {
            repository.authResponse.collect { state ->
                when (state) {
                    is Resource.Empty -> {
                        Log.i(LOG_TAG, "viewModel empty event")
                    }
                    is Resource.Error -> {
                        Log.i(LOG_TAG, "viewModel error event")
                        isEventHandled = false
                        _authEvent.value = AuthEvent.Error(state.message.toString())
                    }
                    is Resource.Success -> {
                        Log.i(LOG_TAG, "viewModel success event ${state.data.toString()}")
                        isEventHandled = false
                        _authEvent.value = AuthEvent.Success(state.data.toString())
                    }
                }
            }
        }
    }

    fun register(userEntries: UserEntries) {
        if (!userEntries.email.trim().isValidEmail()) {
            isEventHandled = false
            _authEvent.value = AuthEvent.WrongEmail
        } else if (!userEntries.password.trim().isValidPassword()) {
            isEventHandled = false
            _authEvent.value = AuthEvent.WrongPassword
        } else repository.register(
            UserEntries(
                userEntries.email.trim(),
                userEntries.password.trim()
            )
        )
    }

    fun login(userEntries: UserEntries) {
        Log.i(LOG_TAG, "login view model")
        if (!userEntries.email.trim().isValidEmail()) {
            Log.i(LOG_TAG, "login view model: wrong email")
            isEventHandled = false
            _authEvent.value = AuthEvent.WrongEmail
        } else if (!userEntries.password.trim().isValidPassword()) {
            Log.i(LOG_TAG, "login view model: wrong password")
            isEventHandled = false
            _authEvent.value = AuthEvent.WrongPassword
        } else {
            Log.i(LOG_TAG, "login view model: success")
            repository.login(
                UserEntries(
                    userEntries.email.trim(),
                    userEntries.password.trim()
                )
            )
        }
    }

}
