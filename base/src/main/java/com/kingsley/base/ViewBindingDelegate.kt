package com.kingsley.base

import android.util.Log
import android.view.LayoutInflater
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
        val type = javaClass.genericSuperclass
        L.e("ViewBindingDelegate", "viewBinding: type = $type, javaClass: $javaClass")
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[viewBindingParameterizedTypePosition] as? Class<VB>
            val method = clazz?.getMethod("inflate", LayoutInflater::class.java)
            return method?.invoke(null, inflater) as? VB
        }
        return null
    }
}