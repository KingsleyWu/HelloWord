package com.kingsley.http

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi


object NetUtils {

    enum class NetState {
        /**
         * 网络不可用
         */
        NETWORK_STATE_UNAVAILABLE,

        /**
         * 网络可用
         */
        NETWORK_STATE_AVAILABLE
    }

    private var mNetworkChangeListeners = mutableListOf<NetworkChangeListener>()

    /**
     * 判断网络是否已连接
     */
    fun isConnected(context: Context): Boolean {
        var connected = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.activeNetwork?.let { network ->
                cm.getNetworkCapabilities(network)?.let {
                    connected = it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            connected = cm?.activeNetworkInfo?.isConnected ?: connected
        }
        return connected
    }

    /**
     * 判断网络类型是否是 Wifi
     */
    fun isWifi(context: Context): Boolean {
        var isWifi = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.activeNetwork?.let { network ->
                cm.getNetworkCapabilities(network)?.let {
                    isWifi = it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            isWifi = cm?.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
        }
        return isWifi
    }

    /**
     * 判断网络类型是否是移动网络
     */
    fun isMobile(context: Context): Boolean {
        var isMobile = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.activeNetwork?.let { network ->
                cm.getNetworkCapabilities(network)?.let {
                    isMobile = it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            isMobile = cm?.activeNetworkInfo?.type == ConnectivityManager.TYPE_MOBILE
        }
        return isMobile
    }

    private val callback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : NetworkCallback() {

        /** 可用网络接入 */
        override fun onAvailable(network: Network) {
            handleOnNetworkChange(NetState.NETWORK_STATE_AVAILABLE)
        }

        /** 网络断开 */
        override fun onLost(network: Network) {
            handleOnNetworkChange(NetState.NETWORK_STATE_UNAVAILABLE)
        }
    }

    private fun handleOnNetworkChange(networkState: NetState) {
        when (networkState) {
            NetState.NETWORK_STATE_UNAVAILABLE ->
                mNetworkChangeListeners.forEach {
                    it.onNetworkChange(false)
                }
            else -> mNetworkChangeListeners.forEach {
                it.onNetworkChange(true)
            }
        }
    }

    /**
     * 注册回调
     */
    fun registerNetworkCallback(context: Context) {
        //7.0及以后 使用这个新的api（7.0以前还是用静态注册广播）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            cm?.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
        }
    }

    /**
     * 注册回调
     */
    fun registerNetworkChangeListener(networkChangeListener: NetworkChangeListener) {
        if (mNetworkChangeListeners.indexOf(networkChangeListener) == -1) {
            mNetworkChangeListeners.add(networkChangeListener)
        }
    }

    /**
     * 注销回调
     */
    fun unregisterNetworkCallback(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            cm?.unregisterNetworkCallback(callback)
        }
    }

    /**
     * 注销回调
     */
    fun unregisterNetworkChangeListener(networkChangeListener: NetworkChangeListener) {
        if (mNetworkChangeListeners.indexOf(networkChangeListener) != -1) {
            mNetworkChangeListeners.remove(networkChangeListener)
        } else {
            return
        }
    }

    interface NetworkChangeListener {
        fun onNetworkChange(available: Boolean)
    }
}