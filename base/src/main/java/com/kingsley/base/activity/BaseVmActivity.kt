package com.kingsley.base.activity

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kingsley.base.ViewModelUtils

abstract class BaseVmActivity<VM : ViewModel>: BaseActivity(){
    /**
     * 是否使用 DataBinding
     */
    open val isUseBind = false

    /**
     * viewModel
     */
    lateinit var mViewModel: VM

    /**
     * 如有需要自定義帶參的 viewModel 需要使用到
     */
    open val factory: ViewModelProvider.Factory? = null

    /**
     * 如實現此 Activity 的子類更改了泛型的位置可以需要使用到此參數更改獲取泛型的位置
     */
    open val parameterizedTypePosition: Int = 0

    /**
     * 佈局 id
     */
    abstract fun layoutId(): Int

    /**
     * 初始化 view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化数据观察者
     */
    abstract fun initObserve()

    override fun onCreate(savedInstanceState: Bundle?) {
        onBeforeCreate()
        super.onCreate(savedInstanceState)
        if (isUseBind) {
            initBind()
        } else {
            setContentView(layoutId())
        }
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        mViewModel = ViewModelUtils.createViewModel(this, factory, parameterizedTypePosition)
        initView(savedInstanceState)
        initObserve()
    }

    /**
     * 供子类始化
     */
    open fun onBeforeCreate() {}

    /**
     * 供子类始化 DataBinding
     */
    open fun initBind() {}
}