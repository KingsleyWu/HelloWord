package com.kingsley.helloword.viewpager2.item

import com.kingsley.base.BaseLifecycleViewModel

class ItemPager2ViewModel: BaseLifecycleViewModel() {
    val data: List<String>
    init {
        data = mutableListOf<String>().apply {
            for (i in 0..1000){
                add("index = $i")
            }
        }
    }
}