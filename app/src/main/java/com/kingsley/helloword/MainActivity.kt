package com.kingsley.helloword

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingsley.base.activity.BaseVbActivity
import com.kingsley.common.EventBus
import com.kingsley.common.EventBus.post
import com.kingsley.common.L
import com.kingsley.common.observeEvent
import com.kingsley.helloword.base.NetworkActivity
import com.kingsley.helloword.bean.StartBean
import com.kingsley.helloword.databinding.MainActivityBinding
import com.kingsley.helloword.notification.NotificationPostService
import com.kingsley.helloword.settings.SettingsActivity
import com.kingsley.helloword.widget.MainAdapter
import com.kingsley.network.NetworkListener
import com.kingsley.network.NetworkUtils
import com.tencent.mmkv.MMKV
import java.util.*

open class MainActivity : BaseVbActivity<MainActivityBinding>(), NetworkListener {
    override val addNetworkListenerOnCreate = false
    private val mMainAdapter: MainAdapter by lazy {
        MainAdapter(DemoUtils.getStartData()) {
            post(it)
        }
    }
    private val mLinearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this) }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mViewBinding.rvMainList.apply {
            layoutManager = mLinearLayoutManager
            adapter = mMainAdapter
        }
        setSupportActionBar(mViewBinding.mainToolbar)
        mViewBinding.tvMenu.text = "設置"
        mViewBinding.tvMenu.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun initData() {
        super.initData()
        observeEvent<StartBean> {
            L.d("wwc observeEvent it = $it")
        }

        observeEvent<StartBean>(isGlobal = true) {
            L.d("wwc observeEvent isGlobal it = $it")
        }
        if (MMKV.defaultMMKV()?.decodeBool("NOTIFICATION_OFF") == false && NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {
            val pm = packageManager
            pm.setComponentEnabledSetting(
                ComponentName(this, NotificationPostService::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
            )
            pm.setComponentEnabledSetting(
                ComponentName(this, NotificationPostService::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
            )
        }
    }

    override fun onNetworkChange(available: Boolean) {
        L.d("wwc onNetworkChange available = $available")
    }

    override fun onDestroy() {
        NetworkUtils.unregisterNetworkCallback(this)
        super.onDestroy()
    }
}

class MainAlias : MainActivity()
class Launcher1 : MainActivity()
class Launcher2 : MainActivity()