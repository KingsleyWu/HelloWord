package com.kingsley.base.adapter

import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * @author Kingsley
 * Created on 2021/6/24.
 */
abstract class BaseAdapter<T, V : BaseViewHolder<T>> @JvmOverloads constructor(open var items: MutableList<T> = mutableListOf()) :
    RecyclerView.Adapter<V>() {
    /**
     * 用于修改 [items] 内容的锁。对內容执行的任何写操作都应在此锁上同步。
     */
    private val mLock = Any()

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: V, position: Int) {
        holder.setData(items[position])
    }

    /**
     * 獲取點個 item
     */
    open fun getItem(position: Int): T {
        return items[position]
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    open fun getPosition(item: T): Int {
        return items.indexOf(item)
    }

    /**
     * 此方法用于直接设置内容
     */
    fun setData(data: List<T>) {
        synchronized(mLock) {
            items.clear()
            items.addAll(data)
        }
        notifyDataSetChanged()
    }


    /**
     * Adds the specified item at the end of the array.
     *
     * @param item The item to add at the end of the array.
     */
    open fun add(item: T) {
        if (item != null) {
            synchronized(mLock) { items.add(item) }
            notifyItemInserted(itemCount)
        }
    }


    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    open fun addAll(collection: Collection<T>) {
        if (collection.isNotEmpty()) {
            synchronized(mLock) { items.addAll(collection) }
            val dataCount = collection.size
            notifyItemRangeInserted(itemCount - dataCount, dataCount)
        }
    }

    /**
     * Adds the specified data at the end of the array.
     *
     * @param data The data to add at the end of the array.
     */
    open fun addAll(data: Array<T>) {
        if (data.isNotEmpty()) {
            synchronized(mLock) {
                items.addAll(data)
            }
            val dataCount = data.size
            notifyItemRangeInserted(itemCount - dataCount, dataCount)
        }
    }

    /**
     * 插入，不会触发任何事情
     *
     * @param item The item to insert into the array.
     * @param index  The index at which the item must be inserted.
     */
    open fun insert(item: T, index: Int) {
        if (item != null) {
            synchronized(mLock) { items.add(item) }
            notifyItemInserted(index)
        }
    }

    /**
     * 插入数组，不会触发任何事情
     *
     * @param data The item to insert into the array.
     * @param index  The index at which the item must be inserted.
     */
    open fun insertAll(data: Array<T>, index: Int) {
        synchronized(mLock) { items.addAll(index, data.asList()) }
        notifyItemRangeInserted(index, data.size)
    }

    /**
     * 插入数组，不会触发任何事情
     *
     * @param data The item to insert into the array.
     * @param index  The index at which the item must be inserted.
     */
    open fun insertAll(data: Collection<T>, index: Int) {
        synchronized(mLock) { items.addAll(index, data) }
        notifyItemRangeInserted(index, data.size)
    }

    open fun update(item: T, pos: Int) {
        synchronized(mLock) { items.set(pos, item) }
        notifyItemChanged(pos)
    }

    /**
     * 删除，不会触发任何事情
     *
     * @param item The item to remove.
     */
    open fun remove(item: T) {
        val position: Int = items.indexOf(item)
        synchronized(mLock) {
            if (items.remove(item)) {
                notifyItemRemoved(position)
            }
        }
    }


    /**
     * 删除，不会触发任何事情
     *
     * @param position The position of the object to remove.
     */
    open fun remove(position: Int) {
        synchronized(mLock) { items.removeAt(position) }
        notifyItemRemoved(position)
    }

    /**
     * 触发清空
     */
    open fun clear() {
        synchronized(mLock) { items.clear() }
        notifyDataSetChanged()
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     * in this adapter.
     */
    open fun sort(comparator: Comparator<in T>) {
        synchronized(mLock) { Collections.sort(items, comparator) }
        notifyDataSetChanged()
    }

}
