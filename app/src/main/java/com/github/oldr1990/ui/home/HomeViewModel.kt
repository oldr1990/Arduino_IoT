package com.github.oldr1990.ui.home

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.oldr1990.data.Constants
import com.github.oldr1990.data.Constants.LOG_TAG
import com.github.oldr1990.model.MappedSensor
import com.github.oldr1990.repository.RepositoryInterface
import com.github.oldr1990.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val repository: RepositoryInterface
) : ViewModel() {
    sealed class HomeScreenEvent {
        class Success(val sensorFirebases: List<MappedSensor>) : HomeScreenEvent()
        class Error(val message: String) : HomeScreenEvent()
        object Empty : HomeScreenEvent()
        object Loading : HomeScreenEvent()
        object NotAuthorized : HomeScreenEvent()
    }

    private var isInitiated = false
    private val _listOfSensors = MutableStateFlow(listOf(MappedSensor("", "", "", "")))
    val listOfSensor: StateFlow<List<MappedSensor>> = _listOfSensors

    private val _homeScreenEvent = MutableStateFlow<HomeScreenEvent>(HomeScreenEvent.Empty)
    val homeScreenEvent: StateFlow<HomeScreenEvent> = _homeScreenEvent

    fun init() {
        if (repository.isAuthorized) {
            if (!isInitiated) {
                isInitiated = true
                Log.i(Constants.LOG_TAG, "View model init start")
                viewModelScope.launch(Dispatchers.IO) {
                    _homeScreenEvent.value = HomeScreenEvent.Loading
                    repository.getListOfSensors()
                    Log.i(Constants.LOG_TAG, "View model viewModelScope")
                    repository.listOfSensors.collect {
                        Log.i(Constants.LOG_TAG, "View model collectt")
                        when (it) {
                            is Resource.Empty -> {
                                _homeScreenEvent.value = HomeScreenEvent.Empty
                                Log.i(Constants.LOG_TAG, "View model empty")
                            }
                            is Resource.Error -> {
                                Log.i(Constants.LOG_TAG, "View model Error")
                                _homeScreenEvent.value =
                                    HomeScreenEvent.Error(it.message.toString())
                            }
                            is Resource.Success -> {
                                Log.i(Constants.LOG_TAG, "View model Success")
                                it.data?.let { list ->
                                    _homeScreenEvent.value = HomeScreenEvent.Success(list)
                                }
                            }
                        }
                    }
                }

            }
        }
        else {
            Log.e(LOG_TAG,"homescreen called: ${repository.isAuthorized}")
            _homeScreenEvent.value = HomeScreenEvent.NotAuthorized
        }
    }

    /*  fun addSensor(name: String, description: String) {

      }*/


}