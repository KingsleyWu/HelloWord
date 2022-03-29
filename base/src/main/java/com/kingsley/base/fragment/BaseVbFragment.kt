package com.kingsley.base.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding

abstract class BaseVbFragment<VB : ViewBinding> : BaseFragment() {

    val handler = Handler(Looper.getMainLooper())

    /**
     * 是否第一次加载
     */
    private var isFirst: Boolean = true

    private var _viewBinding: VB? = null

    /**
     * ViewBinding 注意不能在[recycle]方法後使用此 ViewBinding，否則會報 null
     */
    val mViewBinding get() = _viewBinding!!

    /**
     * 初始化 ViewBinding
     * @param inflater LayoutInflater
     * @param container container
     */
    abstract fun viewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * 初始化 view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化数据观察者
     */
    abstract fun initObserve()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = viewBinding(layoutInflater, container)
        return mViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirst = true
        initView(savedInstanceState)
        initObserve()
        initData()
    }

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            // 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿
            handler.postDelayed( {
                lazyLoadData()
                isFirst = false
            },lazyLoadTime())
        }
    }

    /**
     * Fragment执行onCreate后触发的方法
     */
    open fun initData() {}

    /**
     * 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    open fun lazyLoadTime(): Long {
        return 300
    }

    /**
     * 懒加载
     */
    open fun lazyLoadData(){}

    override fun onDestroyView() {
        super.onDestroyView()
        recycle()
        _viewBinding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}