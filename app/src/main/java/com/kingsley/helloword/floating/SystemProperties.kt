package com.kingsley.helloword.floating

import android.text.TextUtils
import java.lang.Exception
import java.lang.reflect.Method

object SystemProperties {
    private val GET_STRING_PROPERTY = getMethod(getClass("android.os.SystemProperties"))

    private fun getClass(name: String): Class<*>? {
        return try {
            Class.forName(name)
        } catch (e: ClassNotFoundException) {
            try {
                ClassLoader.getSystemClassLoader().loadClass(name)
            } catch (e1: ClassNotFoundException) {
                null
            }
        }
    }

    private fun getMethod(clz: Class<*>?): Method? {
        return if (clz == null) {
            null
        } else try {
            clz.getMethod("get", String::class.java)
        } catch (e: Exception) {
            null
        }
    }

    operator fun get(key: String?): String {
        if (GET_STRING_PROPERTY != null) {
            try {
                val value = GET_STRING_PROPERTY.invoke(null, key) ?: return ""
                return trimToEmpty(value.toString())
            } catch (ignored: Exception) {
            }
        }
        return ""
    }

    operator fun get(key: String?, def: String): String {
        if (GET_STRING_PROPERTY != null) {
            try {
                val value = GET_STRING_PROPERTY.invoke(null, key) as String
                return defaultString(trimToNull(value), def)
            } catch (ignored: Exception) {
            }
        }
        return def
    }

    private fun defaultString(str: String?, defaultStr: String): String {
        return str ?: defaultStr
    }

    private fun trimToNull(str: String): String? {
        val ts = trim(str)
        return if (TextUtils.isEmpty(ts)) null else ts
    }

    private fun trimToEmpty(str: String?): String {
        return str?.trim { it <= ' ' } ?: ""
    }

    private fun trim(str: String?): String? {
        return str?.trim { it <= ' ' }
    }
}