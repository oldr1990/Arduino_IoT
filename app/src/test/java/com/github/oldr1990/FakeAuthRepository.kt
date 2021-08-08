package com.github.oldr1990

/*

class FakeAuthRepository : RepositoryInterface {
    private val _authResponse = MutableStateFlow<Resource<String>>(Resource.Empty())
    override var isAuthorized = false
    override val authResponse: StateFlow<Resource<String>> = _authResponse

    private val _listOfSensors =
        MutableStateFlow<Resource<List<SensorFirebase>>>(Resource.Empty())
    override val listOfSensors: StateFlow<Resource<List<SensorFirebase>>> = _listOfSensors

    private val _sensorDataResponse = MutableStateFlow<Resource<List<BMEData>>>(Resource.Empty())
    override val sensorDataResponse: StateFlow<Resource<List<BMEData>>> = _sensorDataResponse

    override fun login(user: UserEntries) =flow<Resource<String>>{
        if (user.email.isValidEmail())
            if (user.password.isValidPassword()) {
                isAuthorized = true
                _authResponse.value = Resource.Success(USER_ID)
            } else _authResponse.value = Resource.Error(ERROR_INVALID_PASSWORD)
        else _authResponse.value = Resource.Error(ERROR_INVALID_EMAIL)
    }

    override fun register(user: UserEntries) = flow<Resource<String>>{
        if (user.email.isValidEmail())
            if (user.password.isValidPassword()) {
                isAuthorized = true
                _authResponse.value = Resource.Success(USER_ID)
            } else _authResponse.value = Resource.Error(ERROR_INVALID_PASSWORD)
        else _authResponse.value = Resource.Error(ERROR_INVALID_EMAIL)
    }

    override fun logout() {
        isAuthorized = false
    }

    override fun getDataFromBME(from: Long, to: Long) {
    }

    override fun getListOfSensors() {

    }

    override fun addSensor(sensorFirebase: SensorFirebase): Flow<Resource<Boolean>> =
        flow<Resource<Boolean>> {

        }

}*/
