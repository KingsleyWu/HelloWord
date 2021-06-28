package com.kingsley.helloword.threed

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kingsley.base.BaseActivity
import com.kingsley.base.adapter.MultiTypeAdapter
import com.kingsley.helloword.R

/**
 * @author Kingsley
 * Created on 2021/6/24.
 */
class ThreeDActivity : BaseActivity() {
    private val mRvList: RecyclerView by lazy { findViewById(R.id.rv_list) }
    private val mAdapter = MultiTypeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_list_layout)
        mRvList.layoutManager = LinearLayoutManager(this)
        mRvList.adapter = mAdapter
    }
}