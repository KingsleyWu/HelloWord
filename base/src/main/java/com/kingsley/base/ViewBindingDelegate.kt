package com.kingsley.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kingsley.common.L
import java.lang.reflect.ParameterizedType

/**
 * ViewBindingDelegate
 */
interface IViewBindingDelegate<VB : ViewBinding> {
    /**
     * viewBinding 泛型位置
     */
    val viewBindingParameterizedTypePosition: Int
        get() = 0

    /**
     * 初始化 ViewBinding
     * @param inflater LayoutInflater
     */
    @Suppress("UNCHECKED_CAST")
    fun viewBinding(inflater: LayoutInflater): VB? {
        val type = getType(javaClass)
        L.d("ViewBindingDelegate", "viewBinding1: type = $type, javaClass: $javaClass")
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[viewBindingParameterizedTypePosition] as? Class<VB>
            val method = clazz?.getMethod("inflate", LayoutInflater::class.java)
            return method?.invoke(null, inflater) as? VB
        }
        return null
    }

    /**
     * 初始化 ViewBinding
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param attachToRoot attachToRoot
     */
    @Suppress("UNCHECKED_CAST")
    fun viewBinding(inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean): VB? {
        val type = getType(javaClass)
        L.d("ViewBindingDelegate", "viewBinding2: type = $type, javaClass: $javaClass")
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[viewBindingParameterizedTypePosition] as? Class<VB>
            val method = clazz?.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
            return method?.invoke(null, inflater, container, attachToRoot) as? VB
        }
        return null
    }

    /**
     * 為了兼容多繼承，最多 3 層 ！！！
     */
    private fun <T> getType(clazz: Class<T>) = when {
        clazz.genericSuperclass is ParameterizedType -> clazz.genericSuperclass
        clazz.superclass?.genericSuperclass is ParameterizedType -> clazz.superclass.genericSuperclass
        clazz.superclass?.superclass?.genericSuperclass is ParameterizedType -> clazz.superclass?.superclass?.genericSuperclass
        else -> null
    }
}