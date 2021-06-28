package com.kingsley.base.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by kingsley on 2020/9/23
 * item 的代理类 MultiTypeAdapter 通过此类进行事件分发
 */
abstract class ItemViewDelegate<T, VH : BaseViewHolder<T>> {

    abstract fun onCreateViewHolder(context: Context, parent: ViewGroup): VH

    open fun onBindViewHolder(holder: VH, item: T) {
        holder.setData(item)
    }

    open fun onBindViewHolder(holder: VH, item: T, payloads: List<Any>) {
        onBindViewHolder(holder, item)
    }

    fun getPosition(holder: VH): Int = holder.adapterPosition

    open fun onViewRecycled(holder: VH) {}

    open fun onViewAttachedToWindow(holder: VH) {}

    open fun onViewDetachedFromWindow(holder: VH) {}

    open fun getItemId(position: Int, item: T): Long = RecyclerView.NO_ID

    /**
     * 用于 区分 一个class 对应多个 delegate 的方法
     */
    open fun isMach(item: T): Boolean = true
}