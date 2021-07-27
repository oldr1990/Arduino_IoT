package com.github.oldr1990.repository

import android.hardware.Sensor
import android.util.Log
import com.github.oldr1990.data.Constants
import com.github.oldr1990.data.Constants.LOG_TAG
import com.github.oldr1990.model.ArduinoIoTSensor
import com.github.oldr1990.model.BMEData
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val firestore: CollectionReference,
    private val authApi: FirebaseAuth

) :
    RepositoryInterface {

    private val _authResponse = MutableStateFlow<Resource<String>>(Resource.Empty())
    override val authResponse: StateFlow<Resource<String>> = _authResponse

    private val _listOfSensors = MutableStateFlow<Resource<List<ArduinoIoTSensor>>>(Resource.Empty())
    override val listOfSensors: StateFlow<Resource<List<ArduinoIoTSensor>>> = _listOfSensors

    private val _sensorDataResponse = MutableStateFlow<Resource<List<BMEData>>>(Resource.Empty())
    override val sensorDataResponse: StateFlow<Resource<List<BMEData>>> = _sensorDataResponse

    override var isAuthorized = false

    override fun login(user: UserEntries) {
        try {
            if (authApi.currentUser != null) authApi.signOut()
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
        try {
            if (authApi.currentUser != null) authApi.signOut()
            authApi.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        isAuthorized = true
                        _authResponse.value = Resource.Success(authApi.uid.toString())
                    } else
                        _authResponse.value = Resource.Error(it.exception?.message.toString())
                }
        } catch (e: Exception) {
            _authResponse.value = Resource.Error(e.message.toString())
        }
    }

    override fun logout() {
        authApi.signOut()
        if (authApi.currentUser != null) _authResponse.value =
            Resource.Error(Constants.ERROR_UNKNOWN)
        else {
            isAuthorized = false
            _authResponse.value = Resource.Error(Constants.YOU_SIGNOUT)
        }
    }

    override fun getDataFromBME(from: Long, to: Long) {

    }

    override fun getListOfSensors() {
        firestore.whereEqualTo("uid", authApi.currentUser?.uid ?: "")
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    _listOfSensors.value = Resource.Error(it.message.toString())
                    return@addSnapshotListener
                }
                snapshot?.let {
                    _listOfSensors.value = Resource.Success(snapshot.toObjects(ArduinoIoTSensor::class.java))
                }

            }
    }

    override fun addSensor(sensor: Sensor) {
        firestore.add(sensor)
    }
}