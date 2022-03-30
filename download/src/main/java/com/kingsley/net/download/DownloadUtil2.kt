package com.kingsley.net.download

import android.os.Environment
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.kingsley.download.appContext
import com.kingsley.download.base.RetrofitDownload
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.core.IDownloader
import kotlinx.coroutines.CoroutineScope
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

/**
 * 管理下载任务
 */
object DownloadUtil2 {
    var needDownloadSuffix = true

    /**
     * 下載的路徑
     */
    val downloadFolder: String? by lazy {
        appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
    }

    /**
     * 下載器
     */
    var downloader: IDownloader = RetrofitDownload

    /**
     * 當前所有的 DownloadTask
     */
    private val taskMap = ConcurrentHashMap<String, DownloadTask>()

    /**
     * 加入一个任务
     */
    fun add(task: DownloadTask): DownloadTask {
        if (taskMap[task.url] == null) {
            taskMap[task.url] = task
        }
        return task
    }

    /**
     * 通过url获取任务
     */
    fun get(url: String): DownloadTask? {
        return taskMap[url]
    }

    /**
     * 移除一个任务
     */
    fun remove(task: DownloadTask) {
        taskMap.remove(task.url)
    }

    /**
     * 取消任务
     */
    fun cancel(url: String) {
        val task = taskMap.remove(url)
        task?.cancel()
    }

    /**
     * 暫停任务
     */
    fun pause(url: String) {
        val task = taskMap.remove(url)
        task?.pause()
    }

    /**
     * 判断是否存在同任务
     */
    fun contain(url: String): Boolean {
        val task = taskMap[url]
        return task != null
    }

    @JvmStatic
    @JvmOverloads
    fun request(
        url: String?,
        coroutineScope: CoroutineScope = DownloadScope,
        liveData: MutableLiveData<DownloadInfo>? = null,
        action: String? = null,
        type: String? = null,
        flag: String? = null,
        path: String? = null,
        fileName: String? = null,
        group: String? = null,
        childUrl: String? = null,
        data: Serializable? = null
    ): DownloadTask? {
        if (TextUtils.isEmpty(url)) return null
        var downloadTask = taskMap[url]
        if (downloadTask == null) {
            downloadTask = DownloadTask(
                coroutineScope = coroutineScope,
                liveData = liveData,
                url = url!!,
                action = action,
                type = type,
                flag = flag,
                path = path,
                fileName = fileName,
                group = group,
                childUrl = childUrl,
                data = data
            )
            taskMap[url] = downloadTask
        } else {
            downloadTask.coroutineScope = coroutineScope
            downloadTask.liveData = liveData
        }
        return downloadTask
    }

    fun download(
        url: String,
        coroutineScope: CoroutineScope = DownloadScope,
        liveData: MutableLiveData<DownloadInfo>? = null
    ) {
        if (TextUtils.isEmpty(url)) return
        var downloadTask = taskMap[url]
        if (downloadTask == null) {
            downloadTask = DownloadTask(
                coroutineScope = coroutineScope,
                liveData = liveData,
                url = url
            )
            taskMap[url] = downloadTask
        } else {
            downloadTask.coroutineScope = coroutineScope
            downloadTask.liveData = liveData
        }
        downloadTask.start()
    }
}