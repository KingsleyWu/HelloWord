package com.kingsley.base.adapter

/**
 * Created by kingsley on 2020/9/25
 * 注册 一对多的 delegate RegisterBuilder
 */
class RegisterBuilder<T>(private val clazz: Class<T>, private val adapter: MultiTypeAdapter){

    /**
     * 设置 Delegate
     * @param delegates Delegate
     */
    @SafeVarargs
    fun register(vararg delegates: ItemViewDelegate<T, *>): MultiTypeAdapter {
        for (delegate in delegates) {
            adapter.register(clazz, delegate)
        }
        return adapter
    }

}