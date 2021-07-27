package com.github.oldr1990.di

import com.github.oldr1990.data.Constants
import com.github.oldr1990.repository.RepositoryInterface
import com.github.oldr1990.repository.DefaultRepository
import com.github.oldr1990.util.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object MainModule {

    @Singleton
    @Provides
    fun provideAuthFirestore(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): CollectionReference {
        val firestore = FirebaseFirestore.getInstance()
        return firestore.collection(Constants.TEST_SENSOR_TABLE_NAME)
    }

    @Singleton
    @Provides
    fun provideRepository(
        firestore: CollectionReference,
        authFirestore: FirebaseAuth
    ): RepositoryInterface =
        DefaultRepository(firestore, authFirestore)

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