package com.czyzewski.data.network

sealed class Result<Dto> {
    data class Success<Dto>(val data: Dto) : Result<Dto>()
    data class Error(val cause: Throwable) : Result<Nothing>()
}