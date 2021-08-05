package com.kingsley.helloword

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kingsley.base.BaseActivity
import com.kingsley.base.L
import com.kingsley.helloword.base.NetworkActivity
import com.kingsley.helloword.widget.MainAdapter
import com.kingsley.network.NetworkListener
import com.kingsley.network.NetworkUtils

class MainActivity : NetworkActivity() {
    private val mRvMainList: RecyclerView by lazy { findViewById(R.id.rv_main_list) }
    private val mMainAdapter: MainAdapter by lazy { MainAdapter(DemoUtils.getStartData()) }
    private val mLinearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mRvMainList.layoutManager = mLinearLayoutManager
        mRvMainList.adapter = mMainAdapter
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