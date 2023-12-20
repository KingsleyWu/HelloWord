package com.kingsley.helloword.diff

import androidx.recyclerview.widget.DiffUtil


class DiffCallback<T>(
    var oldData: List<T>?,
    var newData: List<T>?,
    val areItemsTheSame: (old: T?, new: T?) -> Boolean = { _, _ -> false },
    val areContentsTheSame: (old: T?, new: T?) -> Boolean = { _, _ -> false },
    val getChangePayload: (old: T?, new: T?) -> Any? = { _, _ -> null }
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldData?.size ?: 0

    override fun getNewListSize() = newData?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldData?.get(oldItemPosition), newData?.get(newItemPosition))
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areContentsTheSame(oldData?.get(oldItemPosition), newData?.get(newItemPosition))
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return getChangePayload(oldData?.get(oldItemPosition), newData?.get(newItemPosition))
    }
}