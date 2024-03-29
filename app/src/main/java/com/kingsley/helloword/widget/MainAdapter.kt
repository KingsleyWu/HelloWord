package com.kingsley.helloword.widget

import android.content.Intent
import android.view.ViewGroup
import android.widget.TextView
import com.kingsley.base.adapter.BaseAdapter
import com.kingsley.base.adapter.BaseViewHolder
import com.kingsley.common.EventBus
import com.kingsley.helloword.R
import com.kingsley.helloword.bean.StartBean

/**
 * @author Kingsley
 * Created on 2021/6/28.
 */
class MainAdapter(data: MutableList<StartBean>, val click:(StartBean) -> Unit) : BaseAdapter<StartBean, MainAdapter.ViewHolder>(data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<StartBean>(parent, R.layout.main_item_layout) {
        private val mTvMainItem: TextView = findViewById(R.id.tv_main_item)
        private var data: StartBean? = null

        init {
            mTvMainItem.setOnClickListener {
                data?.let {
                    mContext.startActivity(Intent(mContext, it.clazz))
                    click(it)
                }
            }
        }

        override fun setData(data: StartBean, payloads: List<Any>) {
            this.data = data
            mTvMainItem.text = data.name
        }
    }

}