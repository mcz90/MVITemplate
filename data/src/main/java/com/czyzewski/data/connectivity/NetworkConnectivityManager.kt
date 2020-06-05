package com.czyzewski.data.connectivity

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build

class ConnectionLostException : Exception()

class NetworkConnectivityManager(private val activity: Activity) {

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        return connectivityManager?.let {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                it.activeNetworkInfo?.isConnected
            } else {
                it.activeNetwork?.let { network ->
                    it.getNetworkCapabilities(network).hasCapability(NET_CAPABILITY_INTERNET)
                }
            }
        } ?: false
    }

}