package com.kingsley.base.fragment

import androidx.fragment.app.Fragment

/**
 * @author Kingsley
 * Created on 2021/6/24.
 */
open class BaseFragment : Fragment() {

    /**
     * 用於釋放
     */
    open fun recycle() {}
}