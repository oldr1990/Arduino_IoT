package com.github.oldr1990.ui.charts

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.oldr1990.model.MappedBMEData
import com.github.oldr1990.repository.RepositoryInterface
import kotlin.random.Random

class ChartsViewModel @ViewModelInject constructor(
   private val repository: RepositoryInterface
): ViewModel() {
    sealed class ChartsEvent{

    }

    fun getBMEData(startTime: Long, sensorID: String){

    }
    fun startTestDataInput(sensorID: String){
        repository.addDataToFirestoreForTest( MappedBMEData(
            Random.nextInt(40, 70).toFloat(),
            Random.nextInt(15, 30).toFloat(),
            Random.nextInt(800, 1050).toFloat(),
            System.currentTimeMillis().toString()
        ),sensorID)
    }
}