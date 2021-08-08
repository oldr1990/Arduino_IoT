package com.github.oldr1990.repository

import com.github.oldr1990.model.MappedBMEData
import com.github.oldr1990.model.MappedSensor
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.model.firebase.SensorFirebase
import com.github.oldr1990.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RepositoryInterface {

    var isAuthorized:Boolean
    val authResponse: StateFlow<Resource<String>>

    val listOfSensors: StateFlow<Resource<List<MappedSensor>>>

    val sensorDataResponseMapped: StateFlow<Resource<List<MappedBMEData>>>

    fun login(user: UserEntries)
    fun register(user: UserEntries)
    fun logout()
    fun getDataFromBME(from: Long, to: Long)
    fun getListOfSensors()
    fun addSensor(sensorFirebase: SensorFirebase): Flow<Resource<Boolean>>
    fun checkDataInDataStore(): UserEntries
    fun addDataToFirestoreForTest(sensorsDataMapped: MappedBMEData, sensorID: String)
}