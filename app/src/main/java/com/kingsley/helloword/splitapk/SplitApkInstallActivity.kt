package com.kingsley.helloword.splitapk

import android.os.Bundle
import com.kingsley.base.activity.BaseVmVbActivity
import com.kingsley.helloword.databinding.SplitApkInstallActivityBinding

class SplitApkInstallActivity: BaseVmVbActivity<SplitApkInstallViewModel, SplitApkInstallActivityBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        with(mViewBinding) {
            btnNativeInstall.setOnClickListener {
                mViewModel.installWithNative(this@SplitApkInstallActivity)
            }
            btnSessionInstall.setOnClickListener {
                mViewModel.installWithSession(this@SplitApkInstallActivity)
            }
        }
    }

    override fun initObserve() {

    }

}