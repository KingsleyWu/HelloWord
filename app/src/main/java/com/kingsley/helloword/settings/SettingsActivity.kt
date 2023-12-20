package com.kingsley.helloword.settings

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingsley.base.activity.BaseVbActivity
import com.kingsley.helloword.DemoUtils
import com.kingsley.helloword.databinding.ActivitySettingsBinding

class SettingsActivity : BaseVbActivity<ActivitySettingsBinding>() {

    private val mAdapter: SettingAdapter by lazy {
        SettingAdapter(DemoUtils.getSettingData()) { data ->

        }
    }
    private val mLinearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this) }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mViewBinding.rvSettingsLayout.apply {
            layoutManager = mLinearLayoutManager
            adapter = mAdapter
        }
    }

    override fun initData() {
        super.initData()
    }

}