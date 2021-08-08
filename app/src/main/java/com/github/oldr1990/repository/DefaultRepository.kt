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
import com.github.oldr1990.model.MappedBMEData
import com.github.oldr1990.model.MappedSensor
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.model.firebase.SensorFirebase
import com.github.oldr1990.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
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

    private val _sensorDataResponse = MutableStateFlow<Resource<List<MappedBMEData>>>(Resource.Empty())
    override val sensorDataResponseMapped: StateFlow<Resource<List<MappedBMEData>>> = _sensorDataResponse

    override var isAuthorized = false

    private val _authResponse = MutableStateFlow<Resource<String>>(Resource.Empty())
    override val authResponse: StateFlow<Resource<String>> = _authResponse


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
                    isAuthorized = true
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
                        isAuthorized = true
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

    override fun getDataFromBME(from: Long, to: Long) {
    }

    override fun getListOfSensors() {
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
                    // snapshot.toObjects(SensorFirebase::class.java).forEach{
                    val documents = snapshot.documents
                    documents.forEach {
                        val sensor = it.toObject(SensorFirebase::class.java)
                        val id = it.id
                        sensor?.let { listOfSensors.add(MappedSensor(sensor.name,sensor.uid,sensor.description,id)) }
                    }
                    Log.i(LOG_TAG, "Repository.getListSensors success")
                    _listOfSensors.value =
                        Resource.Success(listOfSensors)
                }

            }
        Log.i(LOG_TAG, "Repository.getListSensors ends")
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

    override fun checkDataInDataStore(): UserEntries {
        var emailData = ""
        var passwordData = ""
        runBlocking(Dispatchers.IO) {
            dataStore.data.collect {
                emailData = it[emailPreferences] ?: ""
            }
        }
        runBlocking(Dispatchers.IO) {
            dataStore.data.collect {
                passwordData = it[passwordPreferences] ?: ""
            }
        }
        return UserEntries(emailData, passwordData)
    }

    override fun addDataToFirestoreForTest(sensorsDataMapped: MappedBMEData, sensorID: String){
        val document = firestore.document(sensorID).collection(SENSORS_DATA)
        document.add(sensorsDataMapped)
    }
}