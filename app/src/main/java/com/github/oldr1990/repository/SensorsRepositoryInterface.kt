package com.github.oldr1990.repository

import android.hardware.Sensor
import com.github.oldr1990.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow

interface SensorsRepositoryInterface {
    fun getHumidityBME(from: Long, to: Long): MutableStateFlow<Resource<Float>>
    fun getTemperatureBME(from: Long, to: Long): MutableStateFlow<Resource<Float>>
    fun getPressureBME(from: Long, to: Long): MutableStateFlow<Resource<Float>>
    fun getListOfSensors():MutableStateFlow<Resource<List<Sensor>>>
}