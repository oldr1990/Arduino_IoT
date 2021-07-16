package com.github.oldr1990.repository

import com.github.oldr1990.data.Constants
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.util.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val authApi: FirebaseAuth
) :
    AuthRepositoryInterface {
    private val _authState = MutableStateFlow<Resource<String>>(Resource.Empty())
    override val authState: StateFlow<Resource<String>> = _authState

    override fun login(user: UserEntries) {
        if (authApi.currentUser == null) {
            try {
                authApi.signInWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) CoroutineScope(Dispatchers.IO).launch {
                            _authState.value = Resource.Success(authApi.uid.toString())
                        }
                        else
                            _authState.value = Resource.Error(it.exception?.message.toString())
                    }
            } catch (e: Exception) {
                _authState.value = Resource.Error(e.message.toString())
            }
        } else _authState.value = Resource.Error(Constants.ERROR_YOU_ALREADY_AUTHORIZED)
    }

    override fun register(user: UserEntries) {
        if (authApi.currentUser == null) {
            try {
                authApi.createUserWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) CoroutineScope(Dispatchers.IO).launch {
                            _authState.value = Resource.Success(authApi.uid.toString())
                        }
                        else
                            _authState.value = Resource.Error(it.exception?.message.toString())
                    }
            } catch (e: Exception) {
                _authState.value = Resource.Error(e.message.toString())
            }
        } else _authState.value = Resource.Error(Constants.ERROR_YOU_ALREADY_REGISTER)
    }

    override fun logout() {
        authApi.signOut()
        if (authApi.currentUser != null) _authState.value = Resource.Error(Constants.ERROR_UNKNOWN)
        else _authState.value = Resource.Error(Constants.YOU_SIGNOUT)
    }
}