package com.github.oldr1990.repository

import android.hardware.Sensor
import com.github.oldr1990.model.BMEData
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface RepositoryInterface {
    var isAuthorized:Boolean
    val authResponse: StateFlow<Resource<String>>
    val listOfSensors: StateFlow<Resource<List<Sensor>>>
    val sensorDataResponse: StateFlow<Resource<List<BMEData>>>
    fun login(user: UserEntries)
    fun register(user: UserEntries)
    fun logout()
    fun getDataFromBME(from: Long, to: Long)
    fun getListOfSensors()
}