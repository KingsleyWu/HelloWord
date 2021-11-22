package com.kingsley.tetris;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingsley.tetris.bean.RecordBean;
import com.kingsley.tetris.util.TimeUtils;

import java.util.List;


public class RecordAdapter<T> extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {
    private List<T> list;

    public RecordAdapter() {
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_record, viewGroup, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int i) {
        RecordBean recordBean = (RecordBean) list.get(i);
        holder.tvScore.setText(recordBean.getScore());
        holder.tvUserName.setText(recordBean.getName());
        holder.tvTime.setText(TimeUtils.getDefaultTime(Long.parseLong(recordBean.getTime())));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvScore;
        TextView tvUserName;
        TextView tvTime;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
