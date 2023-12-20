package com.kingsley.base.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kingsley.base.ViewModelUtils

abstract class BaseVmFragment<VM : ViewModel> : BaseFragment() {

    val handler = Handler(Looper.getMainLooper())

    /**
     * 如有需要自定義帶參的 viewModel 需要使用到
     */
    open val factory: ViewModelProvider.Factory? = null

    /**
     * 如實現此 Fragment 的子類更改了泛型的位置可以需要使用到此參數更改獲取泛型的位置
     */
    open val parameterizedTypePosition: Int = 0

    /**
     * 是否是共享的 ViewModel
     */
    open val isShareViewModel: Boolean = false

    /**
     * 是否第一次加载
     */
    private var isFirst: Boolean = true

    /**
     * ViewModel
     */
    private var _viewModel: VM? = null

    /**
     * 注意不能在[recycle]方法後使用此 ViewModel，否則會報 null
     */
    val mViewModel get() = _viewModel!!

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirst = true
        _viewModel = ViewModelUtils.createViewModel(
            this,
            factory,
            parameterizedTypePosition,
            isShareViewModel
        )
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
            handler.postDelayed({
                lazyLoadData()
                isFirst = false
            }, lazyLoadTime())
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
    open fun lazyLoadData() {}

    override fun onDestroyView() {
        super.onDestroyView()
        recycle()
        _viewModel = null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}