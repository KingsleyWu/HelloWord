package com.kingsley.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.kingsley.base.IViewBindingDelegate

abstract class BaseVmVbFragment<VM : ViewModel, VB : ViewBinding> : BaseVmFragment<VM>(), IViewBindingDelegate<VB> {

    override val viewBindingParameterizedTypePosition: Int = 1

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
        _viewBinding = viewBinding(layoutInflater, container, false)
        return mViewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}