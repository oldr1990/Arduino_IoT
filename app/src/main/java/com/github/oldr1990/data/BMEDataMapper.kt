package com.github.oldr1990.data

import com.github.oldr1990.model.MappedBMEData
import com.github.oldr1990.model.firebase.BMEDataFirebase
import com.github.oldr1990.util.EntityMapper
import javax.inject.Inject

class BMEDataMapper
@Inject constructor() : EntityMapper<MappedBMEData, BMEDataFirebase> {
    override fun mapFromEntity(entity: MappedBMEData): BMEDataFirebase =
        BMEDataFirebase(
            humidity = entity.humidity.toString(),
            temperature = entity.temperature.toString(),
            pressure = entity.pressure,
            date = entity.date.toLong(),
        )

    override fun mapToEntity(domainModel: BMEDataFirebase): MappedBMEData =
        MappedBMEData(
            humidity = domainModel.humidity.toFloat(),
            temperature = domainModel.temperature.toFloat(),
            pressure = domainModel.pressure,
            date = domainModel.date.toString(),
        )

    fun mapListFromEntity(listToMap: List<BMEDataFirebase>): List<MappedBMEData> =
        listToMap.map {
            mapToEntity(it)
        }
}