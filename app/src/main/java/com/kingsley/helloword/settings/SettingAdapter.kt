package com.kingsley.helloword.settings

import android.view.ViewGroup
import android.widget.TextView
import com.kingsley.base.adapter.BaseAdapter
import com.kingsley.base.adapter.BaseViewHolder
import com.kingsley.helloword.R
import com.kingsley.helloword.bean.SettingBean
import com.tencent.mmkv.MMKV

class SettingAdapter(data: MutableList<SettingBean>, val click:(SettingBean) -> Unit) : BaseAdapter<SettingBean, SettingAdapter.ViewHolder>(data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    inner class ViewHolder(parent: ViewGroup): BaseViewHolder<SettingBean>(parent, R.layout.main_item_layout) {
        private val mTvMainItem: TextView = findViewById(R.id.tv_main_item)
        private var data: SettingBean? = null

        init {
            mTvMainItem.setOnClickListener {
                data?.let{
                    click(it)
                    if (it.name.startsWith("Notification")) {
                        MMKV.defaultMMKV()?.let { mmkv ->
                            mmkv.putBoolean("NOTIFICATION_OFF", !mmkv.decodeBool("NOTIFICATION_OFF"))
                            val off = mmkv.decodeBool("NOTIFICATION_OFF")
                            val data = SettingBean("Notification 監聽 - ${if (off) "已關閉" else "已打開"}")
                            update(data, getPosition(it))
                        }
                    }
                }
            }
        }

        override fun setData(data: SettingBean, payloads: List<Any>) {
            this.data = data
            mTvMainItem.text = data.name
        }
    }

}