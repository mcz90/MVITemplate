package com.czyzewski.data.error

import com.czyzewski.data.connectivity.NetworkConnectivityManager
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

interface IErrorMapper {
    fun map(errorTag: ErrorTag, cause: Throwable): ErrorModel
}

class ErrorMapper(private val connectivityManager: NetworkConnectivityManager) : IErrorMapper {

    override fun map(errorTag: ErrorTag, cause: Throwable): ErrorModel {
        // TODO handle db error
        return when (cause) {
            is UnknownHostException,
            is ConnectException,
            is SocketException,
            is SocketTimeoutException,
            is SSLException -> when (connectivityManager.isNetworkAvailable()) {
                true -> ErrorModel.NoInternetError(errorTag)
                false -> ErrorModel.ConnectionLostError(errorTag, "Connection Lost")
            }
            is HttpException -> mapHttpException(errorTag, cause)
            else -> ErrorModel.UnhandledError(errorTag)
        }
    }

    private fun mapHttpException(errorTag: ErrorTag, exception: HttpException): ErrorModel {
        return ErrorModel.HttpError(
            errorTag = errorTag,
            code = exception.code(),
            errorMessage = exception.message()
        )
    }
}
