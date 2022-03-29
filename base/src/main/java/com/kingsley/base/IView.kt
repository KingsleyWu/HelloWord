package com.kingsley.base

import androidx.annotation.StringRes

/**
 * @author Kingsley
 * Created on 2021/5/18.
 */
interface IView {
    /**
     * 加載中
     */
    fun showLoading(){}

    /**
     * 無內容
     */
    fun showEmpty(){}

    /**
     * 無網絡
     */
    fun showNoNet(){}

    /**
     * 顯示吐司
     */
    fun showToast(msg: String?){}

    /**
     * 顯示吐司
     */
    fun showToast(@StringRes msgResId: Int){}
}