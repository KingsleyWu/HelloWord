package com.kingsley.skin

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import androidx.annotation.AnyRes

/**
 * 前缀或后缀加载资源
 */
class SkinSuffixResources(resources: Resources) {
    /** 默认的 Resources */
    var mResources: Resources = resources
    /** 包名 */
    var mSkinPkgName = ""
    /** 皮肤名称 */
    var mSkinName = ""
    /** 是否是默认皮肤 */
    var isDefaultSkin = true
    /** 是否是前缀加载 默认后缀加载 isSuffix */
    var isPrefix = false

    /**
     * 恢复初始值
     */
    fun reset() {
        mSkinName = ""
        isDefaultSkin = true
        isPrefix = false
    }

    /**
     * 获取对应的resId
     * @param context context
     * @param resId resId
     */
    fun getTargetResId(context: Context, resId: Int): Int {
        return try {
            var resName: String? = null
            if (!isDefaultSkin) {
                resName = if (isPrefix){
                    "${mSkinName}_${mResources.getResourceEntryName(resId)}"
                }else{
                    "${mResources.getResourceEntryName(resId)}_${mSkinName}"
                }
            }
            if (resName.isNullOrEmpty()) {
                resName = context.resources.getResourceEntryName(resId)
            }
            val type: String = context.resources.getResourceTypeName(resId)
            mResources.getIdentifier(resName, type, mSkinPkgName)
        } catch (e: Exception) {
            // 换肤失败不至于应用崩溃.
            0
        }
    }

    private fun getSkinColor(context: Context, resId: Int): Int {
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != 0) {
                @Suppress("DEPRECATION")
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mResources.getColor(targetResId, context.theme)
                } else mResources.getColor(targetResId)
            }
        }
        @Suppress("DEPRECATION")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.resources.getColor(resId, context.theme)
        } else context.resources.getColor(resId)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun getSkinColorStateList(context: Context, resId: Int): ColorStateList {
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != 0) {
                @Suppress("DEPRECATION")
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mResources.getColorStateList(targetResId, context.theme)
                } else mResources.getColorStateList(targetResId)
            }
        }
        @Suppress("DEPRECATION")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.resources.getColorStateList(resId, context.theme)
        } else context.resources.getColorStateList(resId)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getSkinDrawable(context: Context, resId: Int): Drawable {
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != 0) {
                @Suppress("DEPRECATION")
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mResources.getDrawable(targetResId, context.theme)
                } else mResources.getDrawable(targetResId)
            }
        }
        @Suppress("DEPRECATION")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.resources.getDrawable(resId, context.theme)
        } else context.resources.getDrawable(resId)
    }

    private fun getSkinXml(context: Context, resId: Int): XmlResourceParser? {
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != 0) {
                return mResources.getXml(targetResId)
            }
        }
        return context.resources.getXml(resId)
    }

    private fun getSkinValue(context: Context, @AnyRes resId: Int, outValue: TypedValue, resolveRefs: Boolean) {
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != 0) {
                mResources.getValue(targetResId, outValue, resolveRefs)
                return
            }
        }
        context.resources.getValue(resId, outValue, resolveRefs)
    }

    /**
     * 获取对应的Color
     * @param context context
     * @param resId resId
     */
    fun getColor(context: Context, resId: Int): Int {
        return getSkinColor(context, resId)
    }

    /**
     * 获取对应的ColorStateList
     * @param context context
     * @param resId resId
     */
    fun getColorStateList(context: Context, resId: Int): ColorStateList {
        return getSkinColorStateList(context, resId)
    }

    /**
     * 获取对应的Drawable
     * @param context context
     * @param resId resId
     */
    fun getDrawable(context: Context, resId: Int): Drawable {
        return getSkinDrawable(context, resId)
    }

    /**
     * 获取对应的Xml
     * @param context context
     * @param resId resId
     */
    fun getXml(context: Context, resId: Int): XmlResourceParser? {
        return getSkinXml(context, resId)
    }

    /**
     * 获取对应的Value
     * @param context context
     * @param resId resId
     * @param outValue outValue
     * @param resolveRefs resolveRefs
     */
    fun getValue(context: Context, @AnyRes resId: Int, outValue: TypedValue, resolveRefs: Boolean) {
        getSkinValue(context, resId, outValue, resolveRefs)
    }
}