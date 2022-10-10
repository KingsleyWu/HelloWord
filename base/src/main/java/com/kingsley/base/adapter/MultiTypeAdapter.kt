package com.kingsley.base.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.collection.forEach
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlin.reflect.KClass

/**
 * Created by kingsley on 2020/9/23
 */
open class MultiTypeAdapter @JvmOverloads constructor(open var items: MutableList<Any> = mutableListOf()) :
    Adapter<BaseViewHolder<Any>>() {
    /** 注册的 class */
    private val mClassCache: MutableList<Class<*>> by lazy { mutableListOf() }

    /** class 对应的 Delegates */
    private val mDelegates: MutableMap<Class<*>, SparseArrayCompat<ItemViewDelegate<*, *>>> by lazy { mutableMapOf() }

    /** viewType 对应的 Delegate */
    private val viewTypeDelegates: SparseArrayCompat<ItemViewDelegate<*, *>> by lazy { SparseArrayCompat<ItemViewDelegate<*, *>>() }

    /** 上一个 viewType 对应的 值 */
    private var mLastViewType: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Any> {
        return getDelegate(viewType).onCreateViewHolder(parent.context, parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Any>, position: Int) {
        getDelegate(holder.itemViewType).onBindViewHolder(holder, items[position])
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Any>, position: Int, payloads: MutableList<Any>) {
        getDelegate(holder.itemViewType).onBindViewHolder(holder, items[position], payloads)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long {
        return getDelegate(getItemViewType(position)).getItemId(position, items[position])
    }

    override fun onViewRecycled(holder: BaseViewHolder<Any>) {
        getDelegate(holder.itemViewType).onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: BaseViewHolder<Any>): Boolean {
        return getDelegate(holder.itemViewType).onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder<Any>) {
        getDelegate(holder.itemViewType).onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<Any>) {
        getDelegate(holder.itemViewType).onViewDetachedFromWindow(holder)
    }

    override fun getItemViewType(position: Int): Int {
        val item = items.getOrNull(position)
        getItemViewType(item)?.let {
            return it
        }
        //找不到匹配的 viewType
        throw NullPointerException(
            "No delegate added that matches at position = $position in data source, class name = ${item?.javaClass}"
        )
    }

    private fun getItemViewType(item: Any?): Int? {
        item?.let {
            mDelegates[item.javaClass]?.forEach { viewType, delegate ->
                @Suppress("UNCHECKED_CAST")
                delegate as ItemViewDelegate<Any, BaseViewHolder<Any>>
                if (delegate.isMach(item)) {
                    return viewType
                }
            }
        }
        return null
    }

    /**
     * 通過 viewType 獲取 delegate
     */
    fun getDelegate(viewType: Int): ItemViewDelegate<Any, BaseViewHolder<Any>> {
        @Suppress("UNCHECKED_CAST")
        return viewTypeDelegates[viewType] as ItemViewDelegate<Any, BaseViewHolder<Any>>
    }

    /**
     * 此方法用于直接设置内容
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Any>) {
        items = ArrayList(data)
        notifyDataSetChanged()
    }

    /**
     * 注册 一对一 delegate
     *
     * tips： 此方法会覆盖 之前一对多 注册的 delegate
     */
    fun <T> register(clazz: Class<T>, delegate: ItemViewDelegate<T, *>): Int {
        var viewType = mClassCache.indexOf(clazz)
        if (viewType == -1) {
            mClassCache.add(clazz)
        }
        var delegates = mDelegates[clazz]
        if (delegates == null) {
            delegates = SparseArrayCompat()
            viewType = mLastViewType + 1
        } else {
            val index = delegates.indexOfValue(delegate)
            viewType = if (index != -1) index else mLastViewType + 1
        }
        if (viewType > mLastViewType) {
            mLastViewType = viewType
        }
        delegates.put(viewType, delegate)
        mDelegates[clazz] = delegates
        viewTypeDelegates.put(viewType, delegate)
        return viewType
    }

    /**
     * 注册 一对一 delegate
     *
     * tips： 此方法会覆盖 之前一对多 注册的 delegate
     */
    inline fun <reified T : Any> register(delegate: ItemViewDelegate<T, *>): MultiTypeAdapter {
        register(T::class.java, delegate)
        return this
    }

    /**
     * 注册 一对一 delegate
     *
     * tips： 此方法会覆盖 之前一对多 注册的 delegate
     */
    fun <T : Any> register(clazz: KClass<T>, delegate: ItemViewDelegate<T, *>): MultiTypeAdapter {
        register(clazz.java, delegate)
        return this
    }

    /**
     * 注册 一对多 delegate
     * tips： 此方法会覆盖 之前一对一 注册的 delegate
     */
    fun <T : Any> with(clazz: KClass<T>): RegisterBuilder<T> {
        return with(clazz.java)
    }

    /**
     * 注册 一对多 delegate
     * tips： 此方法会覆盖 之前一对一 注册的 delegate
     */
    fun <T : Any> with(clazz: Class<T>): RegisterBuilder<T> {
        return RegisterBuilder(clazz, this)
    }

    /**
     * 註銷 delegate
     */
    fun <T : Any> unregister(clazz: Class<T>): MultiTypeAdapter {
        val viewType = mClassCache.indexOf(clazz)
        if (viewType != -1) { viewTypeDelegates.remove(viewType) }
        mClassCache.remove(clazz)
        mDelegates.remove(clazz)
        return this
    }
}