package com.kingsley.tetris.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import com.kingsley.tetris.BaseApplication

object SizeUtils {
    /**
     * dp 转 px
     *
     * @param dpValue dp 值
     * @return px 值
     */
    fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px 转 dp
     *
     * @param pxValue px 值
     * @return dp 值
     */
    fun px2dp(pxValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 判断是否是大尺寸屏幕
     *
     * @return
     */
    val isTabletDevice: Boolean
        get() = Resources.getSystem().configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >=
                Configuration.SCREENLAYOUT_SIZE_LARGE

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    val screenWidth: Int
        get() {
            val windowManager = BaseApplication.getInstance()?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

    /**
     * 获取屏幕高度
     * @return
     */
    val screenHeight: Int
        get() {
            val windowManager = BaseApplication.getInstance()?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

    /**
     * 获取状态栏高度
     * @return
     */
    val statusBarHeight: Int
        get() {
            val resources = BaseApplication.getInstance()?.resources
            val resourceId = resources?.getIdentifier("status_bar_height", "dimen", "android")
            return resources?.getDimensionPixelSize(resourceId ?: 0) ?: 0
        }
}