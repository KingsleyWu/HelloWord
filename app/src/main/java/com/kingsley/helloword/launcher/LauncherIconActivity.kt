package com.kingsley.helloword.launcher

import android.os.Bundle
import android.view.LayoutInflater
import com.kingsley.base.activity.BaseVbActivity
import com.kingsley.helloword.*
import com.kingsley.helloword.databinding.LauncherIconActivityBinding

class LauncherIconActivity: BaseVbActivity<LauncherIconActivityBinding>() {

    override fun viewBinding(inflater: LayoutInflater) = LauncherIconActivityBinding.inflate(inflater)

    override fun initView(savedInstanceState: Bundle?) {

        mViewBinding.tvLauncher.setOnClickListener {
            LauncherIconUtil.changeIcon(MainActivity::class.java.simpleName)
        }
        mViewBinding.tvLauncher1.setOnClickListener {
            LauncherIconUtil.changeIcon(Launcher1::class.java.simpleName)
        }
        mViewBinding.tvLauncher2.setOnClickListener {
            LauncherIconUtil.changeIcon(Launcher2::class.java.simpleName)
        }
    }

}