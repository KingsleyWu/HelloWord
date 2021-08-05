package com.kingsley.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 监听网络连接改变
 *
 * @author kingsley
 * @date 2017/8/29
 */
class NetworkChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。
        // wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
        // 通知网络变更

        NetworkUtils.handleOnNetworkChange(if (NetworkUtils.isConnected(context)) NetworkUtils.NetState.NETWORK_STATE_AVAILABLE else NetworkUtils.NetState.NETWORK_STATE_UNAVAILABLE)
    }
}