package com.kingsley.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseVmVbFragment<VM : ViewModel, VB : ViewBinding> : BaseVmFragment<VM>() {

    /**
     * ViewBinding
     */
    private var _viewBind: VB? = null
    val mViewBind get() = _viewBind!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBind = initViewBinding(layoutInflater, container)
        return mViewBind.root
    }

    /**
     * 由於使用了 ViewBinding 就不需要子類複寫此方法了
     */
    override fun layoutId() = -1

    /**
     * 初始化 ViewBinding
     * @param inflater LayoutInflater
     * @param container container
     */
    abstract fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBind = null
    }
}