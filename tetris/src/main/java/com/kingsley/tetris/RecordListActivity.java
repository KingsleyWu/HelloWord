package com.kingsley.tetris;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kingsley.tetris.bean.RecordBean;
import com.kingsley.tetris.bean.RecordListBean;
import com.kingsley.tetris.util.ConfigSPUtils;

import java.util.List;

import static com.kingsley.tetris.util.ConfigSPUtils.RECORDLIST;


public class RecordListActivity extends BaseActivity {
    private RecyclerView rvRecordList;
    private RecordAdapter<RecordBean> recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        initView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRecordList.setLayoutManager(layoutManager);
        recordAdapter = new RecordAdapter<>();
        rvRecordList.setAdapter(recordAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_default_devider));
        rvRecordList.addItemDecoration(dividerItemDecoration);
        String recordList = ConfigSPUtils.getString(getApplication(), RECORDLIST);
        if (!TextUtils.isEmpty(recordList)) {
            Gson gson = new Gson();
            RecordListBean recordListBean = gson.fromJson(recordList, RecordListBean.class);
            List<RecordBean> recordBeanList = recordListBean.getRecordBeanList();
            recordAdapter.setList(recordBeanList);
        }
    }

    private void initView() {
        rvRecordList = (RecyclerView) findViewById(R.id.rv_record_list);
    }
}
