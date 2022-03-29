package com.kingsley.base

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.lang.reflect.ParameterizedType

object ViewModelUtils {
    /**
     * 獲取 Activity 的 ViewModel
     * @param activity 需要獲取 ViewModel 的 Activity
     * @param factory 自定義獲取 ViewModel 的工廠
     * @param position ViewModel 所在泛型的位置
     */
    fun <VM : ViewModel> createViewModel(
        activity: ComponentActivity,
        factory: ViewModelProvider.Factory? = null,
        position: Int
    ): VM {
        val vbClass =
            (activity.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<*>>()

        @Suppress("UNCHECKED_CAST")
        val viewModelClazz = vbClass[position] as Class<VM>
        return viewModel(activity, factory, viewModelClazz)
    }

    /**
     * 獲取 Fragment 的 ViewModel
     * @param fragment 需要獲取 ViewModel 的 fragment
     * @param factory 自定義獲取 ViewModel 的工廠
     * @param position ViewModel 所在泛型的位置
     * @param isShareViewModel 是否是共享 ViewModel，即與 Activity 共享的 ViewModel，true 則獲取的是 Activity 的 ViewModel，否則是 Fragment 的 ViewModel
     */
    fun <VM : ViewModel> createViewModel(
        fragment: Fragment,
        factory: ViewModelProvider.Factory? = null,
        position: Int,
        isShareViewModel: Boolean = false
    ): VM {
        val vbClass =
            (fragment.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<*>>()

        @Suppress("UNCHECKED_CAST")
        val viewModelClazz = vbClass[position] as Class<VM>
        return viewModel(if (isShareViewModel) fragment.requireActivity() else fragment, factory, viewModelClazz)
    }

    /**
     * 獲取 ViewModelStoreOwner 的 ViewModel
     * @param owner 需要獲取 ViewModel 的 ViewModelStoreOwner
     * @param factory 自定義獲取 ViewModel 的工廠
     * @param viewModelClazz ViewModel 的 class
     */
    fun <VM : ViewModel> viewModel(
        owner: ViewModelStoreOwner,
        factory: ViewModelProvider.Factory? = null,
        viewModelClazz: Class<VM>
    ): VM {
        val viewModelProvider = if (factory == null) {
            ViewModelProvider(owner)
        } else {
            ViewModelProvider(owner, factory)
        }
        return viewModelProvider[viewModelClazz]
    }
}