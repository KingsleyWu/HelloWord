package com.kingsley.tetris.bean

data class RecordListBean(
    var recordBeanList: List<RecordBean>? = null
)

data class RecordBean(
    var name: String = "",
    var score: String = "",
    var time: String = ""
)