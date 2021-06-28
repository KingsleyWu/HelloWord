package com.kingsley.helloword.apk

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kingsley.base.BaseActivity
import com.kingsley.helloword.R

/**
 * @author Kingsley
 * Created on 2021/6/23.
 */
class GetApkActivity : BaseActivity() {
    private val mTabGetApk: TabLayout by lazy { findViewById(R.id.tab_get_apk) }
    private val mVpGetApk: ViewPager2 by lazy { findViewById(R.id.vp_get_apk) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_apk_activity)
        mVpGetApk.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> GetApkItemFragment.getInstance(USER)
                    1 -> GetApkItemFragment.getInstance(SYSTEM)
                    else -> GetApkItemFragment.getInstance(ALL_APP)
                }
            }
        }
        val tabLayoutMediator = TabLayoutMediator(mTabGetApk, mVpGetApk) { tab, position ->
            when (position) {
                0 -> tab.text = "User"
                1 -> tab.text = "System"
                else -> tab.text = "All App"
            }
        }
        tabLayoutMediator.attach()
    }
}