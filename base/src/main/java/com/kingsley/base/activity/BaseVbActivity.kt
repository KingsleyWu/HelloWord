package com.kingsley.base.activity

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.kingsley.base.IViewBindingDelegate

/**
 * @author Kingsley
 * Created on 2021/6/24.
 */
abstract class BaseVbActivity<VB : ViewBinding> : BaseActivity(), IViewBindingDelegate<VB> {

    private var _viewBinding: VB? = null

    /**
     * 注意不能在[recycle]方法後使用此 ViewBinding，否則會報 null
     */
    val mViewBinding: VB get() = _viewBinding!!

    /**
     * 初始化 view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        onBeforeCreate()
        super.onCreate(savedInstanceState)
        _viewBinding = viewBinding(layoutInflater)
        setContentView(mViewBinding.root)
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
    open fun initData() {}

    override fun onDestroy() {
        super.onDestroy()
        recycle()
        _viewBinding = null
    }
}