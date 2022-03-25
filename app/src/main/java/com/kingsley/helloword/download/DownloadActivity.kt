package com.kingsley.helloword.download

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.kingsley.base.activity.BaseVmVbActivity
import com.kingsley.helloword.databinding.DownloadActivityBinding

class DownloadActivity: BaseVmVbActivity<DownloadViewModel, DownloadActivityBinding>() {
    private var appHomeAdapter: DownloadAppAdapter? = null

    /**
     * App列表数据Observer
     */
    private val appListDataObserver: Observer<MutableList<App>> = Observer {
        appHomeAdapter?.setData(it)
    }

    override fun initViewBinding(inflater: LayoutInflater) = DownloadActivityBinding.inflate(inflater)


    override fun initView(savedInstanceState: Bundle?) {
        mViewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        val itemAnimator = mViewBinding.recyclerView.itemAnimator
        if (itemAnimator is SimpleItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }
        appHomeAdapter = DownloadAppAdapter(lifecycleOwner = this)
        mViewBinding.recyclerView.adapter = appHomeAdapter
    }

    override fun initObserve() {
        mViewModel.appListData.observe(this, appListDataObserver)
        mViewModel.requestAppListData()
    }



}