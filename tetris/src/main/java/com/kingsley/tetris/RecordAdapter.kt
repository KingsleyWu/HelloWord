package com.kingsley.tetris

import com.kingsley.tetris.util.TimeUtils.getDefaultTime
import com.kingsley.tetris.RecordAdapter.RecordViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.kingsley.tetris.bean.RecordBean
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordAdapter<T> : RecyclerView.Adapter<RecordViewHolder>() {
    private var list: List<T>? = null
    fun setList(list: List<T>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecordViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_record, viewGroup, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, i: Int) {
        val (name, score, time) = list!![i] as RecordBean
        holder.tvScore.text = score
        holder.tvUserName.text = name
        holder.tvTime.text = getDefaultTime(time.toLong())
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvScore: TextView = itemView.findViewById(R.id.tv_score)
        var tvUserName: TextView = itemView.findViewById(R.id.tv_user_name)
        var tvTime: TextView = itemView.findViewById(R.id.tv_time)

    }
}