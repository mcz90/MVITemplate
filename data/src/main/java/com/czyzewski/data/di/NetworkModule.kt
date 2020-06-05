package com.czyzewski.data.di

import android.app.Activity
import com.czyzewski.data.BuildConfig
import com.czyzewski.data.connectivity.NetworkConnectivityManager
import com.czyzewski.data.error.ErrorMapper
import com.czyzewski.data.network.ApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@UnstableDefault
val networkModule = module {

    single {
        OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    single {
        val contentType = "application/json".toMediaType()
        Json(JsonConfiguration(ignoreUnknownKeys = true)).asConverterFactory(contentType)
    }

    single<ApiService> {
        Retrofit.Builder()
            .addConverterFactory(get())
            .baseUrl(BuildConfig.API_URL)
            .client(get())
            .build()
            .create(ApiService::class.java)
    }
    single { (activity: Activity) -> NetworkConnectivityManager(activity) }
    single { (activity: Activity) -> ErrorMapper(get(parameters = { parametersOf(activity) })) }
}
