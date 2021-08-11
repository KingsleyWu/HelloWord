package com.kingsley.network

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.kingsley.extend.DefaultActivityLifecycleCallbacks

object NetworkUtils {

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

    /** 回調監聽 */
    private var mNetworkListeners = mutableListOf<NetworkListener>()

    /** 網絡回調監聽 */
    private val mNetworkLifecycleCallbacks = object : DefaultActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activity is NetworkListener && activity.addNetworkListenerOnCreate){
                addNetworkListener(activity)
            }
        }

        override fun onActivityResumed(activity: Activity) {
            if (activity is NetworkListener && !activity.addNetworkListenerOnCreate) {
                addNetworkListener(activity)
            }
        }

        override fun onActivityStopped(activity: Activity) {
            if (activity is NetworkListener && !activity.addNetworkListenerOnCreate) {
                removeNetworkListener(activity)
            }
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (activity is NetworkListener && activity.addNetworkListenerOnCreate) {
                removeNetworkListener(activity)
            }
        }
    }

    /**
     * 判断网络是否已连接
     */
    fun isConnected(context: Context): Boolean {
        var connected = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.activeNetwork?.let { network ->
                cm.getNetworkCapabilities(network)?.let {
                    connected = it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ||
                            it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                            it.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ||
                            it.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && (it.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI_AWARE
                    ) || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 && it.hasTransport(
                        NetworkCapabilities.TRANSPORT_LOWPAN
                    ))
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

    /** 21 （6.0） 版本後使用 NetworkCallback */
    private var mNetworkCallback: NetworkCallback? = null

    /** 21 （6.0） 版本前使用 BroadcastReceiver */
    private var mNetworkChangedReceiver: NetworkChangedReceiver? = null

    /**
     * 回調 處理
     *
     * @param networkState 狀態
     */
    fun handleOnNetworkChange(networkState: NetState) {
        when (networkState) {
            NetState.NETWORK_STATE_UNAVAILABLE ->
                mNetworkListeners.forEach {
                    it.onNetworkChange(false)
                }
            else -> mNetworkListeners.forEach {
                it.onNetworkChange(true)
            }
        }
    }

    /**
     * 注意：只會註冊一次，後面的註冊監聽最好使用
     * {@link #addNetworkListener}
     * 注册回调
     */
    fun registerNetworkCallback(app: Application) {
        app.registerActivityLifecycleCallbacks(mNetworkLifecycleCallbacks)
        val appCtx = app.applicationContext
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mNetworkCallback == null -> {
                mNetworkCallback = NetworkChangeCallback(appCtx)
                registerNetworkCallback(appCtx, mNetworkCallback!!)
            }
            Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && mNetworkChangedReceiver == null -> {
                mNetworkChangedReceiver = NetworkChangedReceiver()
                registerNetworkReceiver(appCtx, mNetworkChangedReceiver!!)
            }
        }
    }

    /**
     * 添加回调
     */
    fun addNetworkListener(networkListener: NetworkListener) {
        if (mNetworkListeners.indexOf(networkListener) == -1) {
            mNetworkListeners.add(networkListener)
        }
    }

    /**
     * 注意：如使用了此方法，所有的 mNetworkChangeListeners 都會清空
     * 注销回调
     */
    fun unregisterNetworkCallback(context: Context) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                val cm =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                mNetworkCallback?.let {
                    cm?.unregisterNetworkCallback(it)
                    mNetworkCallback = null
                }
            }
            else -> {
                mNetworkChangedReceiver?.let {
                    context.applicationContext.unregisterReceiver(it)
                    mNetworkChangedReceiver = null
                }
            }
        }
        mNetworkListeners.clear()
    }

    /**
     * 移除單獨的回调
     */
    fun removeNetworkListener(networkListener: NetworkListener): Boolean {
        return if (mNetworkListeners.indexOf(networkListener) != -1) {
            mNetworkListeners.remove(networkListener)
        } else {
            false
        }
    }

    /**
     * 注册回调
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun registerNetworkCallback(context: Context, networkCallback: NetworkCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    cm?.registerDefaultNetworkCallback(networkCallback)
                }
                else -> {
                    val builder = NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                        .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                    cm?.registerNetworkCallback(builder.build(), networkCallback)
                }
            }
        }
    }

    /**
     * 注销回调
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun unregisterNetworkCallback(context: Context, networkCallback: NetworkCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val cm =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            cm?.unregisterNetworkCallback(networkCallback)
        }
    }

    /**
     * 注册回调
     */
    fun registerNetworkReceiver(context: Context, networkReceiver: BroadcastReceiver) {
        // 注册网络变化监听
        context.registerReceiver(
            networkReceiver,
            @Suppress("DEPRECATION")
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    /**
     * 注销回调
     */
    fun unregisterNetworkReceiver(context: Context, networkReceiver: BroadcastReceiver) {
        context.unregisterReceiver(networkReceiver)
    }
}