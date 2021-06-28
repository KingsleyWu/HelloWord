package com.kingsley.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by kingsley on 2020/10/15
 * BaseViewHolder
 */
abstract class BaseViewHolder<M>(private val item: View) : RecyclerView.ViewHolder(item) {

    constructor(parent: ViewGroup, @LayoutRes res: Int) : this(
            LayoutInflater.from(parent.context).inflate(res, parent, false)
    )

    /**
     * findView
     */
    fun <T : View> findViewById(id: Int): T {
        return item.findViewById(id)
    }

    /**
     * context
     */
    var mContext: Context = itemView.context

    /**
     * 初始化View
     */
    open fun initView() {}

    /**
     * 设置数据
     */
    open fun setData(data: M) {}
}