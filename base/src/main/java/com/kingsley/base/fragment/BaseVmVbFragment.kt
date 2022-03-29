package com.kingsley.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseVmVbFragment<VM : ViewModel, VB : ViewBinding> : BaseVmFragment<VM>() {

    /**
     * 由於使用了 ViewBinding 就不需要子類複寫此方法了
     */
    final override fun layoutId() = -1

    private var _viewBinding: VB? = null

    /**
     * ViewBinding 注意不能在[recycle]方法後使用此 ViewBinding，否則會報 null
     */
    val mViewBinding get() = _viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = viewBinding(layoutInflater, container)
        return mViewBinding.root
    }

    /**
     * 初始化 ViewBinding
     * @param inflater LayoutInflater
     * @param container container
     */
    abstract fun viewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}