package com.kingsley.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author Kingsley
 * Created on 2021/6/8.
 */
@SuppressLint("ObsoleteSdkInt")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class NetworkChangeCallback(private val context: Context) : NetworkCallback() {

    override fun onAvailable(network: Network) {
        NetworkUtils.handleOnNetworkChange(if (NetworkUtils.isConnected(context)) NetworkUtils.NetState.NETWORK_STATE_AVAILABLE else NetworkUtils.NetState.NETWORK_STATE_UNAVAILABLE)
    }

    override fun onLost(network: Network) {
        NetworkUtils.handleOnNetworkChange(if (NetworkUtils.isConnected(context)) NetworkUtils.NetState.NETWORK_STATE_AVAILABLE else NetworkUtils.NetState.NETWORK_STATE_UNAVAILABLE)
    }
}