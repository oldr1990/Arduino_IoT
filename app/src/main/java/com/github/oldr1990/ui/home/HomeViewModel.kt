package com.github.oldr1990.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.oldr1990.model.ArduinoIoTSensor
import com.github.oldr1990.repository.RepositoryInterface
import com.github.oldr1990.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val repository: RepositoryInterface
) : ViewModel() {
    sealed class HomeScreenEvent {
        class Success(arduinoIoTSensors: List<ArduinoIoTSensor>) : HomeScreenEvent()
        class Error(message: String) : HomeScreenEvent()
        object Empty : HomeScreenEvent()
        object Loading : HomeScreenEvent()
        object Logout : HomeScreenEvent()
    }

    private val _homeScreenEvent = MutableStateFlow<HomeScreenEvent>(HomeScreenEvent.Empty)
    val homeScreenEvent: StateFlow<HomeScreenEvent> = _homeScreenEvent

    init {
        viewModelScope.launch {
            repository.listOfSensors.collect {
                when (it) {
                    is Resource.Empty -> {
                    }
                    is Resource.Error -> _homeScreenEvent.value =
                        HomeScreenEvent.Error(it.message.toString())
                    is Resource.Success -> {
                        it.data?.let { list ->
                            _homeScreenEvent.value = HomeScreenEvent.Success(list)
                        }
                    }
                }
            }
        }
    }

}