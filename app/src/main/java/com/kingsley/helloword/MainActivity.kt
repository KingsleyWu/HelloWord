package com.kingsley.helloword

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kingsley.base.*
import com.kingsley.base.adapter.BaseAdapter
import com.kingsley.helloword.bean.StartBean
import com.kingsley.helloword.ui.WhirlingView
import com.kingsley.helloword.navigation.MainViewModel
import com.kingsley.helloword.widget.MainAdapter

class MainActivity : BaseActivity() {
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

}