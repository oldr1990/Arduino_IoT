package com.github.oldr1990.di

import com.github.oldr1990.data.Constants
import com.github.oldr1990.repository.RepositoryInterface
import com.github.oldr1990.repository.DefaultRepository
import com.github.oldr1990.util.DispatcherProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(ActivityComponent::class)
@Module
object MainModule {


    @Singleton
    @Provides
    fun provideFirebaseFirestore(): CollectionReference {
        val firestore = FirebaseFirestore.getInstance()
        return firestore.collection(Constants.TEST_SENSOR_TABLE_NAME)
    }

    @Provides
    fun provideAuthRepository(): RepositoryInterface = DefaultRepository()

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}