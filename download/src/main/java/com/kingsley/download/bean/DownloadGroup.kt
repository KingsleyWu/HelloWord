package com.kingsley.download.bean

import android.text.TextUtils
import com.kingsley.download.core2.DownloadScope

data class DownloadGroup(val downloadList: List<DownloadInfo>) {

    /**
     * 下载的 DownloadScope
     */
    var downloadScopeList: List<DownloadScope>? = null

    internal fun buildDownloadScopeList(changeListener: ((DownloadInfo) -> Unit)? = null, build: (DownloadInfo) -> DownloadScope): List<DownloadScope> {
        if (downloadScopeList == null) {
            downloadScopeList = downloadList.map { build(it) }
        } else {
            downloadScopeList?.forEach {
                it.changeListener = changeListener
            }
        }
        return downloadScopeList!!
    }

    /**
     * 是否是空的
     */
    fun isNotEmpty() = downloadList.isNotEmpty()

    /**
     * 第一个下载信息
     */
    fun first(): DownloadInfo = downloadList[0]

    /**
     * 下一个下载的 DownloadScope
     */
    fun nextScope(last: DownloadInfo): DownloadScope? {
        var nextScope: DownloadScope? = null
        downloadScopeList?.let {
            for (downloadScope in it) {
                if (TextUtils.equals(last.childUrl, downloadScope.downloadInfo.url)) {
                    nextScope = downloadScope
                }
                if (nextScope != null) {
                    break
                }
            }
        }
        return nextScope
    }

    /**
     * 下一个下载的信息
     */
    fun next(last: DownloadInfo): DownloadInfo? {
        var nextDownload: DownloadInfo? = null
        if (isNotEmpty()) {
            for (downloadInfo in downloadList) {
                nextDownload = if (TextUtils.equals(last.childUrl, downloadInfo.url)) downloadInfo else null
                if (nextDownload != null) {
                    break
                }
            }
        }
        return nextDownload
    }

    /**
     * 是否已经下载完
     */
    fun isDone(): Boolean {
        var isDone = true
        if (isNotEmpty()) {
            for (downloadInfo in downloadList) {
                isDone = when (downloadInfo.status) {
                    DownloadInfo.DONE -> true
                    else -> false
                }
                if (!isDone) {
                    break
                }
            }
        }
        return isDone
    }

    /**
     * 是否可以下载
     */
    fun canDownload(): Boolean {
        var canDownload = false
        if (isNotEmpty()) {
            for (downloadInfo in downloadList) {
                canDownload = when (downloadInfo.status) {
                    DownloadInfo.DONE -> false
                    else -> true
                }
                if (canDownload) {
                    break
                }
            }
        }
        return canDownload
    }

    /**
     * 是否是等待任务
     */
    fun isWaiting(): Boolean {
        var isWaiting = false
        if (isNotEmpty()) {
            for (downloadInfo in downloadList) {
                isWaiting = when (downloadInfo.status) {
                    DownloadInfo.WAITING -> true
                    else -> false
                }
                if (isWaiting) {
                    break
                }
            }
        }
        return isWaiting
    }

    /**
     * 是否是正在下载的任务
     */
    fun isLoading(): Boolean {
        var isLoading = false
        if (isNotEmpty()) {
            for (downloadInfo in downloadList) {
                isLoading = when (downloadInfo.status) {
                    DownloadInfo.LOADING -> true
                    else -> false
                }
                if (isLoading) {
                    break
                }
            }
        }
        return isLoading
    }
}