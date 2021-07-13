package com.github.oldr1990.util

sealed class Resource<T>(val data: T?, val message: String?) {
    class Success<T>(data: T) : Resource<T>(data, null)
    class Error<T>(message: String?) : Resource<T>(null, message)
}