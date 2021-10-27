package com.kingsley.shimeji.mascotlibrary

import android.graphics.Bitmap
import com.kingsley.shimeji.AppConstants
import java.lang.Exception
import java.lang.StringBuilder
import java.util.concurrent.atomic.AtomicBoolean


class MascotListing {
    var author: String? = null
    var category: String? = null
    private val downloadInProgress: AtomicBoolean = AtomicBoolean(false)
    var id = 0
    var name: String? = null
    var thumbnail: Bitmap? = null
        get() {
            val bitmap = field
            if (bitmap != null) return bitmap
            throw Exception("Listing Thumbnail bitmap was not set")
        }

    val url: String
        get() {
            val stringBuilder = StringBuilder()
            stringBuilder.append(AppConstants.SERVER_BASE_PATH)
            stringBuilder.append("thumb/")
            stringBuilder.append(id)
            return stringBuilder.toString()
        }

    fun isDownloadInProgress(): Boolean {
        return downloadInProgress.get()
    }

    fun notifyDownloadFinished() {
        downloadInProgress.set(false)
    }

    fun startDownload() {
        downloadInProgress.set(true)
    }
}
