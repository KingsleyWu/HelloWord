package com.kingsley.sample.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kingsley.sample.bean.DiffBean
import com.kingsley.sample.databinding.ItemDiffDemoLayoutBinding

class DiffDemoAdapter(var items: List<DiffBean>?) : RecyclerView.Adapter<DiffDemoAdapter.ViewHolder>() {

    class ViewHolder(val viewBind: ItemDiffDemoLayoutBinding) : RecyclerView.ViewHolder(viewBind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemDiffDemoLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.viewBind){
            items?.get(holder.bindingAdapterPosition)?.let {
                tvItemId.text = it.id.toString()
                tvItemIndex.text = it.content?.index?.toString() ?: ""
                tvItemValue.text = it.content?.value ?: ""
            }
        }
    }

    override fun getItemCount() = items?.size ?: 0
}