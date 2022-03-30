package com.kingsley.helloword.viewpager2.item

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kingsley.base.fragment.BaseVmVbFragment
import com.kingsley.helloword.databinding.ItemPager2FragmentBinding
import com.kingsley.helloword.databinding.SimpleFragmentBinding
import com.kingsley.helloword.viewpager2.SimpleCoordinatorActivity

class ItemPager2Fragment : BaseVmVbFragment<ItemPager2ViewModel, ItemPager2FragmentBinding>() {

    companion object {
        const val KEY_NAME = "name"

        fun getInstance(name: String) : ItemPager2Fragment {
            return ItemPager2Fragment().apply {
                arguments = Bundle().also {
                    it.putString(KEY_NAME, name)
                }
            }
        }
    }

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ItemPager2FragmentBinding.inflate(inflater, container, false)

    override fun initView(savedInstanceState: Bundle?) {
        mViewBinding.tvName.text = arguments?.getString(KEY_NAME) ?: "null"
        mViewBinding.tvName.setOnClickListener {
            startActivity(Intent(context, SimpleCoordinatorActivity::class.java))
        }
    }

    override fun initObserve() {

    }

}