package com.kingsley.sample.recyclerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kingsley.base.BaseViewModel
import com.kingsley.sample.bean.DiffBean
import com.kingsley.sample.bean.DiffItemBean

class DiffDemoViewModel : BaseViewModel() {
    private val _diffData = MutableLiveData<List<DiffBean>>()
    val diffData: LiveData<List<DiffBean>> = _diffData
    private lateinit var data: MutableList<DiffBean>
    var isLoading = false

    private var refreshData: MutableList<DiffBean> = ArrayList<DiffBean>().apply {
        add(DiffBean(0, DiffItemBean(0, "value 0")))
        add(DiffBean(1, DiffItemBean(1, "value 1")))
        add(DiffBean(2, DiffItemBean(2, "value 2")))
        add(DiffBean(3, DiffItemBean(3, "value 3")))
        add(DiffBean(4, DiffItemBean(4, "value 4")))
        add(DiffBean(5, DiffItemBean(5, "value 5")))
        add(DiffBean(6, DiffItemBean(6, "value 6")))
        add(DiffBean(7, DiffItemBean(7, "value 7")))
        add(DiffBean(8, DiffItemBean(8, "value 8")))
        add(DiffBean(9, DiffItemBean(9, "value 9")))
    }

    private var addData: List<DiffBean> = ArrayList<DiffBean>().apply {
        add(DiffBean(10, DiffItemBean(10, "value 10")))
        add(DiffBean(11, DiffItemBean(11, "value 11")))
        add(DiffBean(12, DiffItemBean(12, "value 12")))
        add(DiffBean(13, DiffItemBean(13, "value 13")))
        add(DiffBean(14, DiffItemBean(14, "value 14")))
        add(DiffBean(15, DiffItemBean(15, "value 15")))
        add(DiffBean(16, DiffItemBean(16, "value 16")))
        add(DiffBean(17, DiffItemBean(17, "value 17")))
        add(DiffBean(18, DiffItemBean(18, "value 18")))
        add(DiffBean(19, DiffItemBean(19, "value 19")))
    }

    fun getData(isRefresh: Boolean) {
        launchOnUI(1000) {
            if (isRefresh) {
                data = refreshData.toMutableList()
            } else {
                data.addAll(addData)
            }
            _diffData.postValue(data.toList())
        }

    }

    fun changeData() {
        data[0] = DiffBean(0, DiffItemBean(0, "change value 0"))
        _diffData.postValue(data.toMutableList())
    }
}