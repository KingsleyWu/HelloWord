package com.kingsley.helloword.base

import android.os.Bundle
import com.kingsley.base.BaseActivity
import com.kingsley.network.NetworkListener
import com.kingsley.network.NetworkUtils

/**
 * 帶有網絡監聽的 Activity
 * @author Kingsley
 * Created on 2021/6/29.
 */
abstract class NetworkActivity : BaseActivity(), NetworkListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkUtils.addNetworkListener(this)
    }

    override fun onDestroy() {
        NetworkUtils.removeNetworkListener(this)
        super.onDestroy()
    }
}