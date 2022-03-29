package com.kingsley.skin

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import androidx.annotation.RequiresApi

/**
 * 皮肤资源加载器
 *
 * 拦截Resource读取资源的方法，先
 *
 */
@Suppress("DEPRECATION")
class SkinResourcesProxy(resources: Resources) : Resources(resources.assets, resources.displayMetrics, resources.configuration) {

    companion object {
        const val TAG = "SkinResourcesProxy"
    }

    /**
     * 加载皮肤资源的Resource
     */
    private val skinResource: Resources?
        get() {
            return SkinManager.getCurrentSkinResource()
        }

    private val skinPackageName: String?
        get() {
            return SkinManager.skinPackageName
        }

    /**
     * 根据资源ID，在皮肤包里加载一次
     */
    private fun getResourceIdInSkin(id: Int): Int {
        return skinResource?.run {
            try {
                // 根据资源ID获取在类型
                val type = this@SkinResourcesProxy.getResourceTypeName(id)

                // 根据资源ID获取资源名
                val entryName = this@SkinResourcesProxy.getResourceEntryName(id)

                // 包名和
                val packageName = skinPackageName ?: this@SkinResourcesProxy.getResourcePackageName(id)

                // 根据资源类型和资源名获取资源在皮肤包里的新资源ID
                val idInSkin = getIdentifier(entryName, type, packageName)


                Log.i(TAG, "getResourceIdInSkin type:$type, name:$entryName, packageName:$packageName, idInSkin:$idInSkin")

                // 返回资源ID
                idInSkin
            } catch (e: Exception) {
                0
            }
        } ?: 0
    }

    override fun getString(id: Int): String {
        Log.d(TAG, "getString:$id")
        var value = super.getString(id)

        // 拦截一下
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getString(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getString(id: Int, vararg formatArgs: Any): String {
        Log.d(TAG, "getString:$id")
        var value = super.getString(id, formatArgs)

        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getString(idInSkin, formatArgs) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any): String {
        Log.d(TAG, "getQuantityString:$id")
        var value = super.getQuantityString(id, quantity, formatArgs)

        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getString(idInSkin,quantity, formatArgs) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getQuantityString(id: Int, quantity: Int): String {
        Log.d(TAG, "getQuantityString:$id")
        var value = super.getQuantityString(id, quantity)

        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getQuantityString(idInSkin,quantity) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getStringArray(id: Int): Array<String> {
        Log.d(TAG, "getStringArray:$id")
        var value = super.getStringArray(id)

        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getStringArray(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getIntArray(id: Int): IntArray {
        Log.d(TAG, "getIntArray:$id")
        var value = super.getIntArray(id)

        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getIntArray(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }


    override fun getDimension(id: Int): Float {
        Log.d(TAG, "getDimension:$id")
        var value = super.getDimension(id)

        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getDimension(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getDimensionPixelOffset(id: Int): Int {
        Log.d(TAG, "getDimensionPixelOffset:$id")

        var value = super.getDimensionPixelOffset(id)

        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getDimensionPixelOffset(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getDimensionPixelSize(id: Int): Int {

        Log.d(TAG, "getDimensionPixelSize:$id")

        var value = super.getDimensionPixelSize(id)

        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getDimensionPixelSize(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getDrawable(id: Int): Drawable {
        Log.d(TAG, "getDrawable:$id")
        var value = super.getDrawable(id)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getDrawable(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getDrawable(id: Int, theme: Theme?): Drawable? {
        Log.d(TAG, "getDrawable:$id, theme:$theme")
        var value = super.getDrawable(id, theme)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getDrawable(idInSkin, theme) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }


    override fun getDrawableForDensity(id: Int, density: Int): Drawable? {
        Log.d(TAG, "getDrawableForDensity:$id, density:$density")
        var value = super.getDrawableForDensity(id, density)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getDrawableForDensity(idInSkin, density) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getDrawableForDensity(id: Int, density: Int,  theme: Resources.Theme?): Drawable? {
        Log.d(TAG, "getDrawableForDensity:$id, density:$density")
        var value = super.getDrawableForDensity(id, density, theme)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getDrawableForDensity(idInSkin, density) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getColor(id: Int): Int {
        var value = super.getColor(id)
        val idInSkin  = getResourceIdInSkin(id)
        Log.d(TAG, "getColor id:$id, orgValue:$value, idInSkin:$idInSkin")
        if (idInSkin != 0) {
            try {
                value = skinResource?.getColor(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getColor(id: Int, theme: Resources.Theme?): Int {
        var value = super.getColor(id, theme)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getColor(idInSkin, theme) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun getColorStateList(id: Int): ColorStateList {
        var value = super.getColorStateList(id)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getColorStateList(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getColorStateList(id: Int, theme: Resources.Theme?): ColorStateList {
        var value = super.getColorStateList(id, theme)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getColorStateList(idInSkin, theme) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getBoolean(id: Int): Boolean {
        var value = super.getBoolean(id)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getBoolean(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getInteger(id: Int): Int {
        var value = super.getInteger(id)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getInteger(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    @SuppressLint("NewApi")
    override fun getFloat(id: Int): Float {
        var value = super.getFloat(id)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getFloat(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getLayout(id: Int): XmlResourceParser {
        var value = super.getLayout(id)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getLayout(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getAnimation(id: Int): XmlResourceParser {
        var value = super.getAnimation(id)
        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getAnimation(idInSkin) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

    override fun getValue(id: Int, outValue: TypedValue, resolveRefs: Boolean) {
        var value = super.getValue(id, outValue, resolveRefs)

        val idInSkin  = getResourceIdInSkin(id)
        if (idInSkin != 0) {
            try {
                value = skinResource?.getValue(idInSkin, outValue, resolveRefs) ?: value
            } catch (e: Exception) {
                // ignore
            }
        }
        return value
    }

}