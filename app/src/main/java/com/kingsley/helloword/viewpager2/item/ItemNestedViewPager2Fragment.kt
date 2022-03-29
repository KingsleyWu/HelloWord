package com.kingsley.helloword.viewpager2.item

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kingsley.base.adapter.BaseAdapter
import com.kingsley.base.adapter.BaseViewHolder
import com.kingsley.base.fragment.BaseVmVbFragment
import com.kingsley.helloword.databinding.ItemNestedPager2FragmentBinding

class ItemNestedViewPager2Fragment: BaseVmVbFragment<ItemPager2ViewModel, ItemNestedPager2FragmentBinding>() {

    companion object {
        const val KEY_NAME = "name"

        fun getInstance(name: String) : ItemNestedViewPager2Fragment {
            return ItemNestedViewPager2Fragment().apply {
                arguments = Bundle().also {
                    it.putString(KEY_NAME, name)
                }
            }
        }
    }

    private lateinit var mAdapter: BaseAdapter<String, BaseViewHolder<String>>

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ItemNestedPager2FragmentBinding.inflate(inflater, container, false)

    override fun initView(savedInstanceState: Bundle?) {
        mViewBinding.run {
            swipeRefresh.setOnRefreshListener {
                mAdapter.items = mViewModel.data.toMutableList()
                mAdapter.notifyItemRangeChanged(0, mAdapter.itemCount)
                swipeRefresh.isRefreshing = false
            }
            recyclerView.layoutManager = LinearLayoutManager(context)
            mAdapter = object : BaseAdapter<String, BaseViewHolder<String>>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): BaseViewHolder<String> {
                    return object : BaseViewHolder<String>(TextView(parent.context).apply {
                        layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100)
                        gravity = Gravity.CENTER
                    }) {
                        override fun setData(data: String) {
                            (itemView as? TextView)?.text = data
                        }
                    }
                }
            }
            mAdapter.items = mViewModel.data.toMutableList()
            recyclerView.adapter = mAdapter
        }
    }

    override fun initObserve() {
    }

}