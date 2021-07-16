package com.github.oldr1990.repository

import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface AuthRepositoryInterface {
    val authState: StateFlow<Resource<String>>
    fun login(user: UserEntries)
    fun register(user: UserEntries)
    fun logout()
}