package com.czyzewski.data.error

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

typealias ErrorTag = String

@Serializable
sealed class ErrorModel(@Transient open val errorTag: ErrorTag? = null) : Throwable() {

    @Serializable
    data class HttpError(override val errorTag: ErrorTag, val code: Int, val errorMessage: String) : ErrorModel(errorTag)

    @Serializable
    data class DatabaseError(override val errorTag: ErrorTag, val errorMessage: String) : ErrorModel(errorTag)

    @Serializable
    data class UnhandledError(override val errorTag: ErrorTag) : ErrorModel(errorTag)

    @Serializable
    data class NoInternetError(override val errorTag: ErrorTag) : ErrorModel(errorTag)

    @Serializable
    data class ConnectionLostError(override val errorTag: ErrorTag, val errorMessage: String) : ErrorModel(errorTag)
}