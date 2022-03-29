package com.kingsley.helloword.viewpager2

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kingsley.base.activity.BaseVmVbActivity
import com.kingsley.helloword.R
import com.kingsley.helloword.databinding.ItemCoordinatorLayoutFragmentBinding
import com.kingsley.helloword.viewpager2.item.ItemNestedViewPager2Fragment

class SimpleCoordinatorActivity: BaseVmVbActivity<ViewPager2ViewModel, ItemCoordinatorLayoutFragmentBinding>() {

    override fun initViewBinding(inflater: LayoutInflater) =
        ItemCoordinatorLayoutFragmentBinding.inflate(inflater)

    override fun initView(savedInstanceState: Bundle?) {

        // 此處是為了防止登錄後重新請求數據 導致UI異常
        if (mViewBinding.appBarLayout.childCount > 1) {
            mViewBinding.appBarLayout.removeViewAt(0)
        }
        val topView = layoutInflater.inflate(R.layout.layout_coordinator_top, mViewBinding.appBarLayout, false)
        val lps = AppBarLayout.LayoutParams(
            AppBarLayout.LayoutParams.MATCH_PARENT,
            AppBarLayout.LayoutParams.WRAP_CONTENT
        )
        lps.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED

        mViewBinding.appBarLayout.addView(topView, 0, lps)
        mViewBinding.viewPager.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount() = mViewModel.tabList.size

            override fun createFragment(position: Int): Fragment {
                return ItemNestedViewPager2Fragment.getInstance(mViewModel.tabList[position])
            }

        }
        TabLayoutMediator(mViewBinding.tabLayout, mViewBinding.viewPager) { tab, position ->
            tab.text = mViewModel.tabList[position]
        }.attach()
    }

    override fun initObserve() {

    }
}