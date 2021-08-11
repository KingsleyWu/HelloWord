package com.kingsley.helloword

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kingsley.base.BaseActivity
import com.kingsley.base.L
import com.kingsley.helloword.base.NetworkActivity
import com.kingsley.helloword.databinding.MainActivityBinding
import com.kingsley.helloword.widget.MainAdapter
import com.kingsley.network.NetworkListener
import com.kingsley.network.NetworkUtils

class MainActivity : NetworkActivity() {
    override val addNetworkListenerOnCreate = false
    private val mMainAdapter: MainAdapter by lazy { MainAdapter(DemoUtils.getStartData()) }
    private val mLinearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this) }
    private lateinit var mBinding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = MainActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.rvMainList.apply {
            layoutManager = mLinearLayoutManager
            adapter = mMainAdapter
        }
        mMainAdapter.notifyDataSetChanged()
    }

    override fun onNetworkChange(available: Boolean) {
        L.d("wwc onNetworkChange available = $available")
    }

    override fun onDestroy() {
        NetworkUtils.unregisterNetworkCallback(this)
        super.onDestroy()
    }
}