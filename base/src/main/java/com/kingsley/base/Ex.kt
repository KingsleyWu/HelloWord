package com.kingsley.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding


/**
 * @author Kingsley
 * Created on 2021/5/18.
 */
// 顶层函数版本
inline fun <reified T : ViewModel> getViewModel(
    owner: ViewModelStoreOwner,
    configLiveData: T.() -> Unit = {}
): T = ViewModelProvider(owner)[T::class.java].apply { configLiveData() }

// 扩展函数版本
inline fun <reified T : ViewModel> ViewModelStoreOwner.getSelfViewModel(configLiveData: T.() -> Unit = {}): T =
    getViewModel(this, configLiveData)

fun Context.shortToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context.shortToast(@StringRes msg: Int) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context.longToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun Context.longToast(@StringRes msg: Int) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

val Int.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

/**
 * 获取状态栏高度
 *
 * @return 状态栏高度
 */
fun Context.getStatusBarHeight(): Int {
    // 获得状态栏高度
    var result = 0
    try {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
    } catch (e: NotFoundException) {
        e.printStackTrace()
    }
    return result
}

/**
 * 获取虚拟导航键的高度
 *
 * @return 虚拟导航键的高度
 */
fun Context.getNavBarHeight(): Int {
    var result = 0
    try {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
    } catch (e: NotFoundException) {
        e.printStackTrace()
    }
    return result
}

inline val Fragment.windowHeight: Int
    get() {
        return requireActivity().windowHeight
    }

inline val Fragment.windowWidth: Int
    get() {
        return requireActivity().windowWidth
    }

inline val Activity.windowHeight: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            metrics.bounds.height() - insets.bottom - insets.top
        } else {
            val view = window.decorView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                    .getInsets(WindowInsetsCompat.Type.systemBars())
                resources.displayMetrics.heightPixels - insets.bottom - insets.top
            } else {
                val point = Point()
                val wm = windowManager
                @SuppressLint("ObsoleteSdkInt")
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    wm.defaultDisplay.getRealSize(point)
                } else {
                    wm.defaultDisplay.getSize(point)
                }
                point.y
            }
        }
    }

inline val Activity.windowWidth: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            metrics.bounds.width() - insets.left - insets.right
        } else {
            val view = window.decorView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                    .getInsets(WindowInsetsCompat.Type.systemBars())
                resources.displayMetrics.widthPixels - insets.left - insets.right
            } else {
                val point = Point()
                val wm = windowManager
                @SuppressLint("ObsoleteSdkInt")
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    wm.defaultDisplay.getRealSize(point)
                } else {
                    wm.defaultDisplay.getSize(point)
                }
                point.x
            }
        }
    }

/**
 * 获取屏幕的宽度（单位：px）
 * Return the width of screen, in pixel.
 *
 * @return the width of screen, in pixel
 */
fun Context.getScreenWidth(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        ?: return -1
    val point = Point()
    @SuppressLint("ObsoleteSdkInt")
    @Suppress("DEPRECATION")
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            if (this is Activity) {
                display?.getRealSize(point) ?: wm.defaultDisplay.getRealSize(point)
            } else {
                wm.defaultDisplay.getRealSize(point)
            }
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
            wm.defaultDisplay.getRealSize(point)
        }
        else -> {
            wm.defaultDisplay.getSize(point)
        }
    }
    return point.x
}

/**
 * 获取屏幕的高度（单位：px）
 * Return the height of screen, in pixel.
 *
 * @return the height of screen, in pixel
 */
fun Context.getScreenHeight(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        ?: return -1
    val point = Point()
    @SuppressLint("ObsoleteSdkInt")
    @Suppress("DEPRECATION")
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            if (this is Activity) {
                display?.getRealSize(point) ?: wm.defaultDisplay.getRealSize(point)
            } else {
                wm.defaultDisplay.getRealSize(point)
            }
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
            wm.defaultDisplay.getRealSize(point)
        }
        else -> {
            wm.defaultDisplay.getSize(point)
        }
    }
    return point.y
}

fun Fragment.hideSystemBars() {
    val window = requireActivity().window
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Fragment.showSystemBars() {
    val window = requireActivity().window
    WindowCompat.setDecorFitsSystemWindows(window, true)
    ViewCompat.requestApplyInsets(window.decorView)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())
}

fun Activity.hideSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Activity.showSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    ViewCompat.requestApplyInsets(window.decorView)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())
}

fun Activity.setDecorFitsSystemWindows(decorFitsSystemWindows: Boolean) {
    WindowCompat.setDecorFitsSystemWindows(window, decorFitsSystemWindows)
}

fun Fragment.setDecorFitsSystemWindows(decorFitsSystemWindows: Boolean) {
    WindowCompat.setDecorFitsSystemWindows(requireActivity().window, decorFitsSystemWindows)
}

fun Fragment.hideStatusBars() {
    val window = requireActivity().window
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Fragment.showStatusBars() {
    val window = requireActivity().window
    WindowCompat.setDecorFitsSystemWindows(window, true)
    ViewCompat.requestApplyInsets(window.decorView)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.statusBars())
}

fun Activity.hideStatusBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Activity.showStatusBars() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    ViewCompat.requestApplyInsets(window.decorView)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.statusBars())
}

fun Fragment.hideNavigationBars() {
    val window = requireActivity().window
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).hide(WindowInsetsCompat.Type.navigationBars())
}

fun Fragment.showNavigationBars() {
    val window = requireActivity().window
    ViewCompat.requestApplyInsets(window.decorView)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.navigationBars())
}

fun Activity.hideNavigationBars() {
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).hide(WindowInsetsCompat.Type.navigationBars())
}

fun Activity.showNavigationBars() {
    ViewCompat.requestApplyInsets(window.decorView)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.navigationBars())
}

fun Fragment.hideStatusAndNavigationBars() {
    val window = requireActivity().window
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Fragment.showStatusAndNavigationBars() {
    val window = requireActivity().window
    WindowCompat.setDecorFitsSystemWindows(window, true)
    ViewCompat.requestApplyInsets(window.decorView)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
}

fun Activity.hideStatusAndNavigationBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Activity.showStatusAndNavigationBars() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    ViewCompat.requestApplyInsets(window.decorView)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
}

fun View.isKeyboardVisible(): Boolean {
    val insets = ViewCompat.getRootWindowInsets(this)
    return insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
}

fun Activity.showKeyboard() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).show(WindowInsetsCompat.Type.ime())
    } else {
        val imm: InputMethodManager? =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager?
        imm?.showSoftInput(window.decorView, 0)
    }
}

fun Fragment.showKeyboard() {
    val window = requireActivity().window
    // 由於 WindowInsetsControllerCompat.show(WindowInsetsCompat.Type.ime())
    // 在低於 android 11 版本中 使用的是 view = mWindow.findViewById(android.R.id.content);
    // 在低版本中 android.R.id.content 獲取到的 view 無法彈出鍵盤
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).show(WindowInsetsCompat.Type.ime())
    } else {
        val imm: InputMethodManager? =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager?
        imm?.showSoftInput(window.decorView, 0)
    }
}

fun Activity.hideKeyboard() {
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).hide(WindowInsetsCompat.Type.ime())
}

fun Fragment.hideKeyboard() {
    val window = requireActivity().window
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).hide(WindowInsetsCompat.Type.ime())
}

fun Activity.hideSoftInput(): Boolean {
    val imm: InputMethodManager? =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager?
    return imm?.hideSoftInputFromWindow(window.decorView.windowToken, 0) ?: false
}

fun Activity.showSoftInput(): Boolean {
    val imm: InputMethodManager? =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager?
    return imm?.showSoftInput(window.decorView, 0) ?: false
}

fun View.keyboardHeight(): Int {
    val insets = ViewCompat.getRootWindowInsets(this)
    return insets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 0
}

fun Activity.keyboardHeight(): Int {
    val insets = ViewCompat.getRootWindowInsets(window.decorView)
    return insets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 0
}

fun Fragment.keyboardHeight(): Int {
    val insets = ViewCompat.getRootWindowInsets(requireActivity().window.decorView)
    return insets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 0
}

fun View.setMarginStart(start: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        MarginLayoutParamsCompat.setMarginStart(lp, start)
    }
}

fun View.setMarginEnd(end: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        MarginLayoutParamsCompat.setMarginEnd(lp, end)
    }
}

fun View.setMarginTop(top: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        lp.topMargin = top
    }
}

fun View.setMarginBottom(bottom: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        lp.bottomMargin = bottom
    }
}

inline fun <reified T : ViewBinding> viewBinding(layoutInflater: LayoutInflater, parent: ViewGroup?): T {
    val method = T::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    return method.invoke(null, layoutInflater, parent, false) as T
}

inline fun <reified T : ViewBinding> viewBinding(layoutInflater: LayoutInflater): T {
    val method = T::class.java.getMethod("inflate", LayoutInflater::class.java)
    return method.invoke(null, layoutInflater) as T
}