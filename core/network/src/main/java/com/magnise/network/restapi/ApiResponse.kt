package com.magnise.network.restapi



sealed interface ApiResponse<T> {
    data class Success<T>(val data: T): ApiResponse<T>
    data class Failure<T>(val exception: Exception): ApiResponse<T>
}


inline fun <T> apiCall(apiCall: () -> T): ApiResponse<T> {
    return try {
        ApiResponse.Success(data = apiCall())
    } catch (e: Exception){
        ApiResponse.Failure(exception = e)
    }
}