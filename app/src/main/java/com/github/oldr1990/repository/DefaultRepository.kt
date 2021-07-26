package com.github.oldr1990.repository

import android.hardware.Sensor
import android.util.Log
import com.github.oldr1990.data.Constants
import com.github.oldr1990.data.Constants.ERROR_YOU_ALREADY_AUTHORIZED
import com.github.oldr1990.data.Constants.LOG_TAG
import com.github.oldr1990.model.BMEData
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.util.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultRepository () :
    RepositoryInterface {
    private val authApi = FirebaseAuth.getInstance()

    private val _authResponse = MutableStateFlow<Resource<String>>(Resource.Empty())
    override val authResponse: StateFlow<Resource<String>> = _authResponse

    private val _listOfSensors = MutableStateFlow<Resource<List<Sensor>>>(Resource.Empty())
    override val listOfSensors: StateFlow<Resource<List<Sensor>>> = _listOfSensors

    private val _sensorDataResponse = MutableStateFlow<Resource<List<BMEData>>>(Resource.Empty())
    override val sensorDataResponse: StateFlow<Resource<List<BMEData>>> = _sensorDataResponse

    override var isAuthorized = false

    override fun login(user: UserEntries) {
        try {
            authApi.signInWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener {

                        if (it.isSuccessful) {
                            isAuthorized = true
                            Log.i(LOG_TAG, "login succeed")
                            _authResponse.value = Resource.Success(authApi.uid.toString())
                        } else
                            _authResponse.value = Resource.Error(it.exception?.message.toString())

                }
        } catch (e: Exception) {
            _authResponse.value = (Resource.Error(e.message.toString()))
        }

    }

    override fun register(user: UserEntries) {
        if (authApi.currentUser == null) {
            try {
                authApi.createUserWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) CoroutineScope(Dispatchers.IO).launch {
                            isAuthorized = true
                            _authResponse.value = Resource.Success(authApi.uid.toString())
                        }
                        else
                            _authResponse.value = Resource.Error(it.exception?.message.toString())
                    }
            } catch (e: Exception) {
                _authResponse.value = Resource.Error(e.message.toString())
            }
        } else _authResponse.value = Resource.Error(ERROR_YOU_ALREADY_AUTHORIZED)
    }

    override fun logout() {
        authApi.signOut()
        if (authApi.currentUser != null) _authResponse.value = Resource.Error(Constants.ERROR_UNKNOWN)
        else {
            isAuthorized = false
            _authResponse.value = Resource.Error(Constants.YOU_SIGNOUT)
        }
    }

    override fun getDataFromBME(from: Long, to: Long) {

    }

    override fun getListOfSensors() {

    }
}