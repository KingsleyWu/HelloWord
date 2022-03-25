package com.kingsley.helloword.viewpager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.kingsley.base.fragment.BaseVmVbFragment
import com.kingsley.helloword.databinding.SimpleFragmentBinding
import com.kingsley.helloword.viewpager2.item.ItemNestedViewPager2Fragment
import com.kingsley.helloword.viewpager2.item.ItemPager2Fragment

class SimpleViewPagerFragment : BaseVmVbFragment<SimpleFragmentViewModel, SimpleFragmentBinding>() {

    companion object {
        const val KEY_NAME = "name"

        fun getInstance(name: String) : SimpleViewPagerFragment {
            return SimpleViewPagerFragment().apply {
                arguments = Bundle().also {
                    it.putString(KEY_NAME, name)
                }
            }
        }
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = SimpleFragmentBinding.inflate(inflater, container, false)

    override fun initView(savedInstanceState: Bundle?) {
        mViewBinding.viewPager.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount() = mViewModel.tabList.size

            override fun createFragment(position: Int): Fragment {
                return when(position) {
                    3 -> ItemNestedViewPager2Fragment.getInstance(mViewModel.tabList[position])
                    else -> ItemPager2Fragment.getInstance(mViewModel.tabList[position])
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