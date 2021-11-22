package com.kingsley.tetris

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kingsley.tetris.bean.RecordBean
import com.kingsley.tetris.bean.RecordListBean
import com.kingsley.tetris.util.ConfigSPUtils.RECORDLIST
import com.kingsley.tetris.util.ConfigSPUtils.getString

class RecordListActivity : BaseActivity() {
    private var rvRecordList: RecyclerView? = null
    private var recordAdapter: RecordAdapter<RecordBean>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_list)
        initView()
        val layoutManager = LinearLayoutManager(this)
        rvRecordList!!.layoutManager = layoutManager
        recordAdapter = RecordAdapter()
        rvRecordList!!.adapter = recordAdapter
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_default_devider)!!)
        rvRecordList!!.addItemDecoration(dividerItemDecoration)
        val recordList = getString(application, RECORDLIST)
        if (!TextUtils.isEmpty(recordList)) {
            val gson = Gson()
            val (recordBeanList) = gson.fromJson(recordList, RecordListBean::class.java)
            recordAdapter!!.setList(recordBeanList)
        }
    }

    private fun initView() {
        rvRecordList = findViewById<View>(R.id.rv_record_list) as RecyclerView
    }
}