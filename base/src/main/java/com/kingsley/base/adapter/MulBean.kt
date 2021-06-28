package com.kingsley.base.adapter

/**
 * Created by kingsley on 2020/11/2
 * 可用于扩展单个实体类对应多个 delegate ()
 */
class MulBean(var data: Any, var type: Int = 0){

    @Suppress("UNCHECKED_CAST")
    fun <T> data() : T { return data as T }
}