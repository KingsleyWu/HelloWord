package com.kingsley.sample.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.kingsley.sample.bean.DiffBean

class DiffUtilCallBack(var oldData: List<DiffBean>?, var newData: List<DiffBean>?) : DiffUtil.Callback() {

    override fun getOldListSize() = oldData?.size ?: 0

    override fun getNewListSize() = newData?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData?.get(oldItemPosition) == newData?.get(newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData?.get(oldItemPosition) === newData?.get(newItemPosition)
    }
}