package com.kingsley.extend

fun <T : android.os.Parcelable> android.os.Parcel.readParcelableEx(clazz: Class<T>): T? {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        readParcelable(clazz.classLoader, clazz)
    } else {
        @Suppress("DEPRECATION")
        readParcelable(clazz.classLoader)
    }
}
