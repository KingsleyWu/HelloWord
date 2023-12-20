package com.kingsley.network

/**
 * @author Kingsley
 * 網絡變更監聽
 * Created on 2021/6/29.
 */
interface NetworkListener {
    val addNetworkListenerOnCreate: Boolean
        get() = true

    /**
     * 網絡變更
     *
     * @param available true 為網絡可用，false 為網絡不可用
     */
    fun onNetworkChange(available: Boolean)
}