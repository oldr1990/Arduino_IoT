package com.github.oldr1990.repository


import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.oldr1990.data.BMEDataMapper
import com.github.oldr1990.data.Constants.EMPTY_STRING
import com.github.oldr1990.data.Constants.LOG_TAG
import com.github.oldr1990.data.Constants.SENSORS_DATA
import com.github.oldr1990.data.Constants.SENSOR_DATE_FIELD
import com.github.oldr1990.data.Constants.SENSOR_TABLE_NAME
import com.github.oldr1990.model.MappedBMEData
import com.github.oldr1990.model.MappedSensor
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.model.firebase.BMEDataFirebase
import com.github.oldr1990.model.firebase.SensorFirebase
import com.github.oldr1990.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val firestore: CollectionReference,
    private val authApi: FirebaseAuth,
    private val bmeMapper: BMEDataMapper,
    private val dataStore: DataStore<Preferences>
) :
    RepositoryInterface {
    private val emailPreferences = stringPreferencesKey("email")
    private val passwordPreferences = stringPreferencesKey("password")

    private val _listOfSensors =
        MutableStateFlow<Resource<List<MappedSensor>>>(Resource.Empty())
    override val listOfSensors: StateFlow<Resource<List<MappedSensor>>> = _listOfSensors

    private val _sensorDataResponse =
        MutableStateFlow<Resource<List<MappedBMEData>>>(Resource.Empty())
    override val sensorDataResponseMapped: StateFlow<Resource<List<MappedBMEData>>> =
        _sensorDataResponse

    override val getUserEntriesFromDataStore: Flow<UserEntries> =
        dataStore.data.map { data ->
            UserEntries(
                data[emailPreferences] ?: "",
                data[passwordPreferences] ?: ""
            )
        }

    override val isAuthorized: Boolean
        get() {
            return when (_authResponse.value) {
                is Resource.Empty -> false
                is Resource.Error -> false
                is Resource.Success -> true
            }
        }

    private val _authResponse = MutableStateFlow<Resource<String>>(Resource.Empty())
    override val authResponse: StateFlow<Resource<String>> = _authResponse

    private val _userEntriesFromDataStore =
        MutableStateFlow<Resource<UserEntries>>(Resource.Empty())
    override val userEntriesFromDataStore: StateFlow<Resource<UserEntries>> =
        _userEntriesFromDataStore


    override fun login(user: UserEntries) {
        try {
            Log.i(LOG_TAG, "repository login start")
            authApi.signInWithEmailAndPassword(user.email, user.password)
                .addOnFailureListener {
                    Log.i(LOG_TAG, "login fail${it.message.toString()}")
                    _authResponse.value = Resource.Error(it.message.toString())
                }
                .addOnSuccessListener {
                    writeUserEntriesToDataStore(user)
                    //isAuthorized = true
                    Log.i(LOG_TAG, "login succeed emit data")
                    _authResponse.value = Resource.Success(authApi.uid.toString())
                }
        } catch (e: Exception) {
            _authResponse.value = Resource.Error(e.message.toString())
        }
    }

    override fun register(user: UserEntries) {
        try {
            authApi.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        writeUserEntriesToDataStore(user)
                        // isAuthorized = true
                        _authResponse.value = Resource.Success(authApi.uid.toString())
                    } else
                        _authResponse.value = (Resource.Error(it.exception?.message.toString()))
                }
        } catch (e: Exception) {
            _authResponse.value = Resource.Error(e.message.toString())
        }
    }

    override fun logout() {
        authApi.signOut()
    }

    override fun getDataFromBME(from: Long, sensorID: String) {
        try {
            val firestoreSensor = FirebaseFirestore.getInstance()
                .collection("$SENSOR_TABLE_NAME/$sensorID/$SENSORS_DATA")
            firestoreSensor.whereNotEqualTo(SENSOR_DATE_FIELD, from)
                .addSnapshotListener { value, error ->
                    error?.let {
                        Log.e(LOG_TAG, it.message.toString())
                        _sensorDataResponse.value = Resource.Error(it.message)
                        return@addSnapshotListener
                    }
                    value?.let { query ->
                        val firebaseSensorData = ArrayList<BMEDataFirebase>()
                        //  Log.e(LOG_TAG, query.documents.toString())
                        query.documents.forEach {
                            Log.e(LOG_TAG, it.toString())
                            it?.let {
                                it.toObject(BMEDataFirebase::class.java)?.let { it1 ->
                                    firebaseSensorData.add(it1)
                                }
                            }
                        }
                        _sensorDataResponse.value =
                            Resource.Success(bmeMapper.mapListFromEntity(firebaseSensorData))
                    }
                }
        } catch (e: Exception) {
            _sensorDataResponse.value = Resource.Error(e.message)
        }
    }

    override fun getListOfSensors() {
        if (isAuthorized) {
            Log.i(LOG_TAG, "Repository.getListSensors start")
            firestore.whereEqualTo("uid", authApi.currentUser?.uid ?: "")
                .addSnapshotListener { snapshot, error ->
                    error?.let {
                        Log.i(LOG_TAG, "Repository.getListSensors error")
                        _listOfSensors.value = Resource.Error(it.message.toString())
                        return@addSnapshotListener
                    }
                    snapshot?.let {

                        val listOfSensors = ArrayList<MappedSensor>()
                        snapshot.toObjects(SensorFirebase::class.java).forEach {
                            val documents = snapshot.documents
                            documents.forEach {
                                val sensor = it.toObject(SensorFirebase::class.java)
                                val id = it.id
                                sensor?.let {
                                    listOfSensors.add(
                                        MappedSensor(
                                            sensor.name,
                                            sensor.uid,
                                            sensor.description,
                                            id
                                        )
                                    )
                                }
                            }
                            Log.i(LOG_TAG, "Repository.getListSensors success")
                            _listOfSensors.value =
                                Resource.Success(listOfSensors)
                        }
                    }
                }
            Log.i(LOG_TAG, "Repository.getListSensors ends")
        }
    }

    override fun addSensor(sensorFirebase: SensorFirebase): Flow<Resource<Boolean>> =
        flow<Resource<Boolean>> {
            try {
                firestore.add(sensorFirebase).addOnFailureListener { e ->
                    runBlocking { emit(Resource.Error(e.message)) }
                }.addOnSuccessListener {
                    runBlocking { emit(Resource.Success(true)) }
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)

    private fun writeUserEntriesToDataStore(user: UserEntries) {
        if (user.email != EMPTY_STRING && user.password != EMPTY_STRING) {
            CoroutineScope(Dispatchers.IO).launch {
                dataStore.edit { userData ->
                    userData[emailPreferences] = user.email
                    userData[passwordPreferences] = user.password
                }
            }
        }
    }

    override fun addDataToFirestoreForTest(sensorsDataMapped: MappedBMEData, sensorID: String) {
        val document = firestore.document(sensorID).collection(SENSORS_DATA)
        document.add(sensorsDataMapped)
    }
}