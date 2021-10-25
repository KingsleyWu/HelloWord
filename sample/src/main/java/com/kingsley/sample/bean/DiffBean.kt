package com.kingsley.sample.bean

data class DiffBean(var id: Int, var content: DiffItemBean? = null)

data class DiffItemBean(var index: Int, var value: String?)
