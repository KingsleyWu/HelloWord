package com.kingsley.sample.recyclerview

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kingsley.base.activity.BaseVmVbActivity
import com.kingsley.common.L
import com.kingsley.sample.databinding.DiffDemoActivityBinding

class DiffDemoActivity : BaseVmVbActivity<DiffDemoViewModel, DiffDemoActivityBinding>() {
    private val mLayoutManager: LinearLayoutManager by lazy(mode = LazyThreadSafetyMode.NONE) { LinearLayoutManager(this) }
    private val mAdapter: DiffDemoAdapter by lazy(mode = LazyThreadSafetyMode.NONE) { DiffDemoAdapter(null) }
    private val mDiffUtilCallBack: DiffUtilCallBack by lazy(mode = LazyThreadSafetyMode.NONE) {
        DiffUtilCallBack(
            null,
            null
        )
    }

    override fun viewBinding(inflater: LayoutInflater) = DiffDemoActivityBinding.inflate(inflater)

    override fun initView(savedInstanceState: Bundle?) {
        with(mViewBinding) {
            srDiffLayout.setOnRefreshListener {
                mViewModel.getData(true)
            }
            rvDiffLayout.layoutManager = mLayoutManager
            rvDiffLayout.adapter = mAdapter
            rvDiffLayout.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    val lastVisibleItemPosition: Int = mLayoutManager.findLastVisibleItemPosition()
                    val itemCount: Int = mLayoutManager.itemCount
                    if (lastVisibleItemPosition >= itemCount - 3 && dy > 0 && !mViewModel.isLoading) {
                        mViewModel.isLoading = true
                        mViewModel.getData(false)
                    }
                }

                override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {

                }
            })
            tvChanged.setOnClickListener {
                mViewModel.changeData()
            }
        }
    }

    override fun initObserve() {
        mViewModel.diffData.observe(this) {
            L.d("observe wwwwwww ")
            L.println("observe wwwwwww 222222")
            mViewModel.isLoading = false
            mViewBinding.srDiffLayout.isRefreshing = false
            mDiffUtilCallBack.oldData = mAdapter.items
            mDiffUtilCallBack.newData = it
            mAdapter.items = it
            val diffResult = DiffUtil.calculateDiff(mDiffUtilCallBack)
            diffResult.dispatchUpdatesTo(mAdapter)
        }

        L.d("initObserve wwwwwww ")
        L.println("initObserve wwwwwww 222222")
        mViewBinding.srDiffLayout.isRefreshing = true
        mViewModel.getData(true)
    }

}