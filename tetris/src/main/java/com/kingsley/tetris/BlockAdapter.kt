package com.kingsley.tetris

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class BlockAdapter : BaseAdapter() {
    //以后可以自定义颜色
    private var colors: IntArray? = null
    fun setColors(colors: IntArray?) {
        this.colors = colors
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (colors == null) 0 else colors!!.size
    }

    override fun getItem(position: Int): Any {
        return colors!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View, parent: ViewGroup): View {
        val convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_block, parent, false)
        val imageView = convertView.findViewById<ImageView>(R.id.iv_block)
        imageView.isEnabled = colors?.get(position) != 0
        return convertView
    }
}