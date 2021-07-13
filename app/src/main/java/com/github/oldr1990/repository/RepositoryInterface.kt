package com.github.oldr1990.repository

import com.github.oldr1990.util.Resource
import io.grpc.internal.SharedResourceHolder
import kotlinx.coroutines.flow.MutableStateFlow

interface RepositoryInterface {
    fun getHumidity(from: Long, to: Long): MutableStateFlow<Resource<Float>>
    fun getTemperature(from: Long, to: Long): MutableStateFlow<Resource<Float>>
    fun getPressure(from: Long, to: Long): MutableStateFlow<Resource<Float>>

}