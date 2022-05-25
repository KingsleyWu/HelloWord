package com.kingsley.base.activity

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.kingsley.base.IViewBindingDelegate

/**
 * @author Kingsley
 * Created on 2021/6/24.
 */
abstract class BaseVmVbActivity<VM : ViewModel, VB : ViewBinding> : BaseVmActivity<VM>(), IViewBindingDelegate<VB> {

    override val viewBindingParameterizedTypePosition: Int = 1

    /**
     * 由於使用了 ViewBinding 就不需要子類複寫此方法了
     */
    final override fun layoutId() = -1

    private var _viewBinding: VB? = null

    /**
     * 注意不能在[recycle]方法後使用此 ViewBinding，否則會報 null
     */
    val mViewBinding get() = _viewBinding!!

    /**
     * 使用 ViewBinding
     */
    override val isUseBinding = true

    /**
     * 供子类始化 DataBinding, ViewBinding
     */
    override fun initBinding() {
        _viewBinding = viewBinding(layoutInflater)
        setContentView(mViewBinding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewBinding = null
    }
}