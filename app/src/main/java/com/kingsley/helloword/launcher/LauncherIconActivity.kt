package com.kingsley.helloword.launcher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.kingsley.base.activity.BaseVbActivity
import com.kingsley.helloword.databinding.LauncherIconActivityBinding

class LauncherIconActivity: BaseVbActivity<LauncherIconActivityBinding>() {

    override fun initView(savedInstanceState: Bundle?) {

        mViewBinding.tvLauncher.setOnClickListener {
            LauncherIconUtil.changeIcon(LauncherIconUtil.MAIN)
        }
        mViewBinding.tvLauncher1.setOnClickListener {
            LauncherIconUtil.changeIcon(LauncherIconUtil.MAIN1)
        }
        mViewBinding.tvLauncher2.setOnClickListener {
            LauncherIconUtil.changeIcon(LauncherIconUtil.MAIN2)
        }
    }

    companion object {
        fun launch(context: Context){
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("hello://icon"))
            val resolveActivity = intent.resolveActivity(context.packageManager)
            resolveActivity?.let {
                context.startActivity(intent)
            }
        }
    }
}