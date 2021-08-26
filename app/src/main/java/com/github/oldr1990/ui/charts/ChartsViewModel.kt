package com.github.oldr1990.ui.charts

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.oldr1990.model.MappedBMEData
import com.github.oldr1990.repository.RepositoryInterface
import com.github.oldr1990.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChartsViewModel @ViewModelInject constructor(
    private val repository: RepositoryInterface
) : ViewModel() {
    sealed class ChartsEvent {
        class Success(val sensorData: List<MappedBMEData>) : ChartsEvent()
        class Error(val message: String) : ChartsEvent()
        object Empty : ChartsEvent()
        object Loading : ChartsEvent()
    }

    private val _chartsEvent = MutableStateFlow<ChartsEvent>(ChartsEvent.Empty)
    val chartsEvent: StateFlow<ChartsEvent> = _chartsEvent

    fun getBMEData(startTime: Long, sensorID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _chartsEvent.value = ChartsEvent.Loading
            repository.getDataFromBME(startTime, sensorID)
            repository.sensorDataResponseMapped.collect {
                when (it) {
                    is Resource.Empty -> _chartsEvent.value = ChartsEvent.Empty
                    is Resource.Error -> _chartsEvent.value =
                        ChartsEvent.Error(it.message.toString())
                    is Resource.Success -> _chartsEvent.value =
                        ChartsEvent.Success(it.data ?: listOf())
                }
            }
        }

    }

 /*   fun startTestDataInput(sensorID: String) {
        repository.addDataToFirestoreForTest(
            MappedBMEData(
                Random.nextInt(40, 70).toFloat(),
                Random.nextInt(15, 30).toFloat(),
                Random.nextInt(800, 1050).toFloat(),
                System.currentTimeMillis().toString()
            ), sensorID
        )
    }*/
}