package com.kingsley.base.activity

import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

/**
 * @author Kingsley
 * Created on 2021/6/24.
 */
abstract class BaseVmVbActivity<VM : ViewModel, VB : ViewBinding> : BaseVmActivity<VM>() {

    /**
     * ViewBinding
     */
    private var _viewBinding: VB? = null
    val mViewBinding get() = _viewBinding!!

    /**
     * 使用 ViewBinding
     */
    override val isUseBind: Boolean
        get() = true

    /**
     * 初始化 ViewBinding
     * @param inflater LayoutInflater
     */
    abstract fun initViewBinding(inflater: LayoutInflater): VB

    /**
     * 初始化 DataBinding
     */
    override fun initBind() {
        _viewBinding = initViewBinding(layoutInflater)
        setContentView(mViewBinding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewBinding = null
    }

    /**
     * 由於使用了 ViewBinding 就不需要子類複寫此方法了
     */
    override fun layoutId() = -1
}