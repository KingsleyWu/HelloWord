package com.kingsley.skin.listener

/**
 * @author Kingsley
 * Created on 2021/7/2.
 */
interface ILoaderListener {
    fun onStart() {}
    fun onSuccess() {}
    fun onFailed(reason: String?) {}
}