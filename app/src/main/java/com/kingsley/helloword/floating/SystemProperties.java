package com.kingsley.helloword.floating;

import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Created by Vito on 2018/7/5.
 */
public class SystemProperties {
    private static final Method GET_STRING_PROPERTY = getMethod(getClass("android.os.SystemProperties"));

    private static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            try {
                return ClassLoader.getSystemClassLoader().loadClass(name);
            } catch (ClassNotFoundException e1) {
                return null;
            }
        }
    }

    private static Method getMethod(Class<?> clz) {
        if (clz == null) {
            return null;
        }
        try {
            return clz.getMethod("get", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String get(String key) {
        if (GET_STRING_PROPERTY != null) {
            try {
                Object value = GET_STRING_PROPERTY.invoke(null, key);
                if (value == null) {
                    return "";
                }
                return trimToEmpty(value.toString());
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    public static String get(String key, String def) {
        if (GET_STRING_PROPERTY != null) {
            try {
                String value = (String) GET_STRING_PROPERTY.invoke(null, key);
                return defaultString(trimToNull(value), def);
            } catch (Exception ignored) {
            }
        }
        return def;
    }

    private static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    private static String trimToNull(String str) {
        String ts = trim(str);
        return TextUtils.isEmpty(ts) ? null : ts;
    }

    private static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    private static String trim(String str) {
        return str == null ? null : str.trim();
    }
}
