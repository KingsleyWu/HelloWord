package com.kingsley.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.kingsley.base.ViewModelUtils

/**
 * @author Kingsley
 * Created on 2021/6/24.
 */
abstract class BaseVbActivity<VB : ViewBinding> : BaseActivity() {

    /**
     * ViewBinding
     */
    private var _viewBind: VB? = null
    val mViewBind get() = _viewBind!!
    /**
     * 初始化 ViewBinding
     * @param inflater LayoutInflater
     */
    abstract fun initViewBinding(inflater: LayoutInflater): VB

    /**
     * 初始化 view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        onBeforeCreate()
        super.onCreate(savedInstanceState)
        _viewBind = initViewBinding(layoutInflater)
        setContentView(mViewBind.root)
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        initView(savedInstanceState)
        initData()
    }

    /**
     * 供子类始化
     */
    open fun onBeforeCreate() {}

    /**
     * 初始化
     */
    open fun initData(){}

    override fun onDestroy() {
        super.onDestroy()
        _viewBind = null
    }

}