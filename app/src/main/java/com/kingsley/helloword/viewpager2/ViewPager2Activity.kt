package com.kingsley.helloword.viewpager2

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.kingsley.base.activity.BaseVmVbActivity
import com.kingsley.helloword.databinding.ActivityViewpager2Binding
import com.kingsley.helloword.viewpager2.item.ItemCoordinatorLayoutFragment
import com.kingsley.helloword.viewpager2.item.ItemNestedViewPager2Fragment
import com.kingsley.helloword.viewpager2.item.ItemPager2Fragment

class ViewPager2Activity : BaseVmVbActivity<ViewPager2ViewModel, ActivityViewpager2Binding>() {

    override fun initViewBinding(inflater: LayoutInflater) =
        ActivityViewpager2Binding.inflate(inflater)

    override fun initView(savedInstanceState: Bundle?) {
        mViewBinding.viewPager.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount() = mViewModel.tabList.size

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    1 -> ItemCoordinatorLayoutFragment.getInstance(mViewModel.tabList[position])
                    else -> SimpleViewPagerFragment.getInstance(mViewModel.tabList[position])
                }
            }

        }
        TabLayoutMediator(mViewBinding.tabLayout, mViewBinding.viewPager) { tab, position ->
            tab.text = mViewModel.tabList[position]
        }.attach()
    }

    override fun initObserve() {

    }

}