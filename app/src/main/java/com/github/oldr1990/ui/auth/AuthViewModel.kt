package com.github.oldr1990.ui.auth


import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.oldr1990.data.Constants
import com.github.oldr1990.data.Constants.LOG_TAG
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.repository.RepositoryInterface
import com.github.oldr1990.util.Resource
import com.github.oldr1990.util.isValidEmail
import com.github.oldr1990.util.isValidPassword
import kotlinx.coroutines.Dispatchers
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
        object Loading: AuthEvent()
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
                        //  Log.i(LOG_TAG, "viewModel empty event")
                    }
                    is Resource.Error -> {
                        //  Log.i(LOG_TAG, "viewModel error event")
                        isEventHandled = false
                        _authEvent.value = AuthEvent.Error(state.message.toString())
                    }
                    is Resource.Success -> {
                        // Log.i(LOG_TAG, "viewModel success event ${state.data.toString()}")
                        isEventHandled = false
                        _authEvent.value = AuthEvent.Success(state.data.toString())
                    }
                }
            }
            val dataStore = repository.checkDataInDataStore()
            if (dataStore.email != Constants.EMPTY_STRING && dataStore.password != Constants.EMPTY_STRING) {
                login(dataStore)
            }
        }
    }

    fun register(userEntries: UserEntries) {
        try {
            if (!userEntries.email.trim().isValidEmail()) {
                isEventHandled = false
                _authEvent.value = AuthViewModel.AuthEvent.WrongEmail
            } else if (!userEntries.password.trim().isValidPassword()) {
                isEventHandled = false
                _authEvent.value = AuthEvent.WrongPassword
            } else {
                _authEvent.value = AuthEvent.Loading
                viewModelScope.launch(Dispatchers.IO){
                    repository.register(
                        UserEntries(
                            userEntries.email.trim(),
                            userEntries.password.trim()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            _authEvent.value = AuthEvent.Error(e.message.toString())
        }
    }

    fun login(userEntries: UserEntries) {
        try {// Log.i(LOG_TAG, "login view model")
            println("view model start")
            if (!userEntries.email.isValidEmail()) {
                println("login view model: wrong email")
                //   Log.i(LOG_TAG, "login view model: wrong email")
                isEventHandled = false
                _authEvent.value = AuthEvent.WrongEmail
            } else if (!userEntries.password.isValidPassword()) {
                //   Log.i(LOG_TAG, "login view model: wrong password")
                println("login view model: wrong password")
                isEventHandled = false
                _authEvent.value = AuthEvent.WrongPassword
            } else {
                _authEvent.value = AuthEvent.Loading
                println("login view model: success")
                Log.i(LOG_TAG, "login view model: success checking data")
                viewModelScope.launch(Dispatchers.IO) {
                    repository.login(
                        UserEntries(
                            userEntries.email.trim(),
                            userEntries.password.trim()
                        )
                    )
                }
                println("view model end")
            }
        } catch (e: Exception) {
            println("view model exception: ${e.message.toString()}")
            _authEvent.value = AuthEvent.Error(e.message.toString())
        }
    }

}
