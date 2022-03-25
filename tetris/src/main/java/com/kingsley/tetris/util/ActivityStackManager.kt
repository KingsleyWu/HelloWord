package com.kingsley.tetris.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import com.kingsley.tetris.BaseApplication
import java.util.*
import kotlin.system.exitProcess

class ActivityStackManager private constructor() {
    private var mStack: Stack<Activity?> = Stack()

    /**
     * 获取栈的信息
     */
    val stackInfo: String
        get() {
            val sb = StringBuilder()
            for (temp in mStack) {
                if (temp != null) {
                    sb.append(temp.toString()).append("\n")
                }
            }
            return sb.toString()
        }

    /**
     * 将activity加入到栈中
     *
     * @param activity 需要加入到栈中的activity
     */
    fun addActivity(activity: Activity?) {
        mStack.push(activity)
    }

    /**
     * 删除栈中activity
     */
    fun removeActivity(activity: Activity?) {
        mStack.remove(activity)
    }

    /**
     * @return 栈顶的activity
     */
    val activity: Activity?
        get() = if (!mStack.isEmpty()) {
            mStack.peek()
        } else null

    /**
     * 关闭并删除掉最上面一个的activity
     */
    fun finishActivity() {
        if (!mStack.isEmpty()) {
            val temp = mStack.pop()
            temp?.finish()
        }
    }

    /***
     * 关闭并删除指定 activity
     */
    fun finishActivity(activity: Activity?) {
        if (mStack.isEmpty()) {
            return
        }
        try {
            mStack.remove(activity)
        } catch (ignored: Exception) {
        } finally {
            activity?.finish()
        }
    }

    /**
     * 删除并关闭栈中该class对应的所有的该activity
     */
    fun finishAllActivity(clazz: Class<*>) {
        if (!mStack.isEmpty()) {
            val iterator = mStack.iterator()
            while (iterator.hasNext()) {
                val activity = iterator.next()
                if (activity != null && activity.javaClass == clazz) {
                    //注意应该通过iterator操作stack，要不然回报ConcurrentModificationException
                    iterator.remove()
                    activity.finish()
                }
            }
        }
    }

    /**
     * 删除并关闭栈中该class对应的第一个该activity,从栈顶开始
     */
    fun finishLastActivity(clazz: Class<*>) {
        var activity: Activity? = null
        if (!mStack.isEmpty()) {
            for (temp in mStack) {
                if (temp != null && temp.javaClass == clazz) {
                    activity = temp
                }
            }
        }
        activity?.let { finishActivity(it) }
    }

    /**
     * 删除栈上该activity之上的所有activity
     */
    fun finishAfterActivity(activity: Activity?) {
        if (activity != null && mStack.search(activity) == -1) {
            return
        }
        while (mStack.peek() != null) {
            val temp = mStack.pop()
            if (temp != null && temp == activity) {
                mStack.push(temp)
                break
            }
            temp?.finish()
        }
    }

    /**
     * 删除栈上该class之上的所有activity
     */
    fun finishAfterActivity(clazz: Class<*>) {
        var flag = true
        var activity: Activity? = null
        for (value in mStack) {
            activity = value
            if (activity != null && activity.javaClass == clazz) {
                flag = false
                break
            }
        }
        if (flag) {
            return
        }
        finishAfterActivity(activity)
    }

    /**
     * 弹出关闭所有activity并关闭应用所有进程
     */
    fun finishAllActivityAndClose() {
        while (mStack.size > 0) {
            val temp = mStack.pop()
            temp?.finish()
        }
        try {
            val activityManager =
                BaseApplication.getInstance()?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            activityManager?.killBackgroundProcesses(BaseApplication.getInstance()!!.packageName)
        } catch (ignored: SecurityException) {
        }
        exitProcess(0)
    }

    /**
     * 弹出关闭所有activity并保留应用后台进程
     */
    fun finishAllActivityWithoutClose() {
        while (mStack.size > 0) {
            val temp = mStack.pop()
            temp?.finish()
        }
        exitProcess(0)
    }

    companion object {
        @JvmStatic
        @Volatile
        var instance: ActivityStackManager? = null
            get() {
                if (field == null) {
                    synchronized(ActivityStackManager::class.java) {
                        if (field == null) {
                            field = ActivityStackManager()
                        }
                    }
                }
                return field
            }
            private set
    }
}