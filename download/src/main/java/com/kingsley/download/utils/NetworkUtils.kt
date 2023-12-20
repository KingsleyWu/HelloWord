package com.kingsley.download.utils

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Build.VERSION
import com.kingsley.download.appContext

internal object NetworkUtils {

    @Suppress("DEPRECATION")
    fun isWifiAvailable(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (connManager != null) {
            if (VERSION.SDK_INT < 23) {
                return connManager.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
            }
            val activeNetwork = connManager.activeNetwork
            if (activeNetwork != null) {
                val networkCapabilities = connManager.getNetworkCapabilities(activeNetwork)
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }
            }
        }
        return false
    }

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return if (connectivity == null) {
            false
        } else if (VERSION.SDK_INT >= 23) {
            val activeNetwork = connectivity.activeNetwork
            activeNetwork != null && connectivity.getNetworkCapabilities(activeNetwork) != null
        } else {
            connectivity.activeNetworkInfo?.state == NetworkInfo.State.CONNECTED
        }
    }

    @Suppress("DEPRECATION")
    fun getNetworkAvailable(context: Context): Pair<Boolean,Boolean> {
        val cm = context.getSystemService(Service.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return Pair(false, false)
        val isConnectivityAvailable: Boolean
        val isWifiAvailable: Boolean
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = cm.activeNetwork
            if (activeNetwork != null) {
                isConnectivityAvailable = true
                isWifiAvailable = cm.getNetworkCapabilities(activeNetwork)
                    ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    ?: false
            } else {
                isWifiAvailable = false
                isConnectivityAvailable = false
            }
        } else if (cm.activeNetworkInfo != null) {
            isConnectivityAvailable = true
            //wifi active
            isWifiAvailable = cm.activeNetworkInfo!!.type == ConnectivityManager.TYPE_WIFI
        } else {
            isConnectivityAvailable = false
            isWifiAvailable = false
        }
        return Pair(isWifiAvailable, isConnectivityAvailable)
    }
}