package com.github.oldr1990

import android.hardware.Sensor
import com.github.oldr1990.TestConstants.USER_ID
import com.github.oldr1990.data.Constants.ERROR_INVALID_EMAIL
import com.github.oldr1990.data.Constants.ERROR_INVALID_PASSWORD
import com.github.oldr1990.model.BMEData
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.repository.RepositoryInterface
import com.github.oldr1990.util.Resource
import com.github.oldr1990.util.isValidEmail
import com.github.oldr1990.util.isValidPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeAuthRepository: RepositoryInterface{
    private val _authResponse = MutableStateFlow<Resource<String>>(Resource.Empty())
    override var isAuthorized = false
    override val authResponse: StateFlow<Resource<String>> = _authResponse

    private val _listOfSensors = MutableStateFlow<Resource<List<Sensor>>>(Resource.Empty())
    override val listOfSensors: StateFlow<Resource<List<Sensor>>> = _listOfSensors

    private val _sensorDataResponse = MutableStateFlow<Resource<List<BMEData>>>(Resource.Empty())
    override val sensorDataResponse: StateFlow<Resource<List<BMEData>>> = _sensorDataResponse

    override fun login(user: UserEntries) {
        if (user.email.isValidEmail())
            if (user.password.isValidPassword()){
                isAuthorized = true
                _authResponse.value = Resource.Success(USER_ID)
            }
        else _authResponse.value = Resource.Error(ERROR_INVALID_PASSWORD)
        else _authResponse.value = Resource.Error(ERROR_INVALID_EMAIL)
    }

    override fun register(user: UserEntries) {
        if (user.email.isValidEmail())
            if (user.password.isValidPassword()){
                isAuthorized = true
                _authResponse.value = Resource.Success(USER_ID)
            }
            else _authResponse.value = Resource.Error(ERROR_INVALID_PASSWORD)
        else _authResponse.value = Resource.Error(ERROR_INVALID_EMAIL)
    }

    override fun logout() {
        isAuthorized = false
    }

    override fun getDataFromBME(from: Long, to: Long) {
    }

    override fun getListOfSensors() {

    }
}