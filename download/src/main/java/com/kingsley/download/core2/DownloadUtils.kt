package com.kingsley.download.core2

import android.os.Environment
import android.text.TextUtils
import com.kingsley.common.L
import com.kingsley.download.appContext
import com.kingsley.download.base.RetrofitDownload
import com.kingsley.download.bean.DownloadGroup
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.client.RoomClient
import com.kingsley.download.core.IDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

object DownloadUtils {

    /**
     * 最大同時運行的協程數量 3
     */
    private const val MAX_SCOPE = 3
    var needDownloadSuffix = true

    /**
     * 下載器
     */
    var downloader: IDownloader = RetrofitDownload

    /**
     * 下載的路徑
     */
    val downloadFolder: String? by lazy {
        appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
    }

    /**
     * 當前所有的 DownloadScope
     */
    private val scopeMap = ConcurrentHashMap<String, DownloadScope>()

    /**
     * 當前所有的 DownloadGroup
     */
    private val groupMap = ConcurrentHashMap<String, DownloadGroup>()

    /**
     * 當前運行的 DownloadScope
     */
    private val taskScopeMap = ConcurrentHashMap<String, DownloadScope>()

    fun download(
        downloadGroup: DownloadGroup,
        changeListener: ((DownloadInfo?) -> Unit)? = null,
        errorListener: ((String) -> Unit)? = null,
        doneListener: ((DownloadGroup) -> Unit)? = null
    ) {
        val listener: (DownloadInfo) -> Unit = {
            if (downloadGroup.canDownload()) {
                L.d("download status = ${it.status}")
                when (it.status) {
                    // 0 无状态
                    DownloadInfo.NONE -> {

                    }
                    // 1 等待中
                    DownloadInfo.WAITING -> {

                    }
                    // 2 下载中
                    DownloadInfo.LOADING -> {

                    }
                    // 3 暂停
                    DownloadInfo.PAUSE -> {

                    }
                    // 4 错误
                    DownloadInfo.ERROR -> {

                    }
                    // 5 完成
                    DownloadInfo.DONE -> {
                        downloadGroup.nextScope(it)?.let { downloadScope ->
                            if (downloadScope.canDownload()) {
                                downloadScope.start()
                            }
                        }
                    }
                }
                changeListener?.invoke(it)
            } else {
                doneListener?.invoke(downloadGroup)
            }
        }
        if (downloadGroup.canDownload()) {
            var downloadScopeList = downloadGroup.downloadScopeList
            if (downloadScopeList == null) {
                downloadScopeList = downloadGroup.buildDownloadScopeList(listener) {
                    request(it, listener)
                }
            }
            for (downloadScope in downloadScopeList) {
                if (downloadScope.canDownload()) {
                    downloadScope.start()
                } else {
                    listener.invoke(downloadScope.downloadInfo)
                }
            }
        } else if (downloadGroup.isNotEmpty() && downloadGroup.isDone()) {
            doneListener?.invoke(downloadGroup)
        } else {
            errorListener?.invoke("Download info is null!")
        }
    }

    /**
     * 请求一个下载任务[DownloadScope]
     * 这是创建[DownloadScope]的唯一途径,请不要通过其他方式创建[DownloadScope]
     * 首次任务调用此方法获取[DownloadScope]并不会在数据库中生成数据
     * 首次任务只有调用了[DownloadScope.start]并且成功进入[DownloadInfo.WAITING]状态才会在数据库中生成数据
     * 首次任务的判断依据为数据库中是否保留有当前的任务数据
     *
     * @param url 下載的url
     * @param type 文件type
     * @param flag flag
     * @param action 意圖
     * @param path 指定文件下載的path,如果没有指定将会使用默认的下载目录
     * @param fileName 下載後保存文件的名稱
     * @param group 可以用於做識別是否是組合下載
     * @param childUrl 子下載的url
     * @param data 需要下載的數據
     */
    @JvmStatic
    fun request(
        url: String,
        action: String? = null,
        type: String? = null,
        flag: String? = null,
        path: String? = null,
        fileName: String? = null,
        group: String? = null,
        childUrl: String? = null,
        data: Serializable? = null,
        changeListener: ((DownloadInfo) -> Unit)?
    ): DownloadGroup {
        var downloadGroup = groupMap[url]
        if (downloadGroup == null) {
            val downloadInfo = DownloadInfo(
                url = url, action = action, type = type, flag = flag, path = path,
                fileName = fileName, group = group, childUrl = childUrl, data = data
            )
            downloadGroup = DownloadGroup(listOf(downloadInfo))
            downloadGroup.buildDownloadScopeList {
                request(it, changeListener)
            }
            groupMap[url] = downloadGroup
        }
        return downloadGroup
    }


    /**
     * 请求一个下载任务[DownloadScope]
     * 这是创建[DownloadScope]的唯一途径,请不要通过其他方式创建[DownloadScope]
     * 首次任务调用此方法获取[DownloadScope]并不会在数据库中生成数据
     * 首次任务只有调用了[DownloadScope.start]并且成功进入[DownloadInfo.WAITING]状态才会在数据库中生成数据
     * 首次任务的判断依据为数据库中是否保留有当前的任务数据
     *
     * @param downloadInfo 下載的信息
     * @param changeListener 下载回调
     */
    @JvmStatic
    fun request(
        downloadInfo: DownloadInfo,
        changeListener: ((DownloadInfo) -> Unit)? = null,
    ): DownloadScope {
        var downloadScope = scopeMap[downloadInfo.url]
        if (downloadScope == null) {
            downloadScope = DownloadScope(downloadInfo, changeListener).also {
                scopeMap[downloadInfo.url] = it
            }
        }
        return downloadScope
    }

    /**
     * 请求一个下载任务[DownloadScope]
     * 这是创建[DownloadScope]的唯一途径,请不要通过其他方式创建[DownloadScope]
     * 首次任务调用此方法获取[DownloadScope]并不会在数据库中生成数据
     * 首次任务只有调用了[DownloadScope.start]并且成功进入[DownloadInfo.WAITING]状态才会在数据库中生成数据
     * 首次任务的判断依据为数据库中是否保留有当前的任务数据
     *
     * @param url 下載的url
     * @param type 文件type
     * @param flag flag
     * @param action 意圖
     * @param path 指定文件下載的path,如果没有指定将会使用默认的下载目录
     * @param fileName 下載後保存文件的名稱
     * @param group 可以用於做識別是否是組合下載
     * @param childUrl 子下載的url
     * @param data 需要下載的數據
     */
    @JvmStatic
    private suspend fun request(
        url: String,
        action: String? = null,
        type: String? = null,
        flag: String? = null,
        path: String? = null,
        fileName: String? = null,
        group: String? = null,
        childUrl: String? = null,
        data: Serializable? = null,
        changeListener: ((DownloadInfo?) -> Unit)? = null,
    ): DownloadScope? {
        if (TextUtils.isEmpty(url)) return null
        var downloadScope = scopeMap[url]
        if (downloadScope == null) {
            var downloadInfo = withContext(Dispatchers.IO) {
                RoomClient.dataBase.downloadDao().queryByUrl(url)
            }
            //数据库中并没有任务,这是一个新的下载任务
            if (downloadInfo == null) {
                downloadInfo = DownloadInfo(
                    url = url, action = action, type = type, flag = flag, path = path,
                    fileName = fileName, group = group, childUrl = childUrl, data = data
                )
            }
            downloadScope = DownloadScope(downloadInfo, changeListener).also {
                scopeMap[url] = it
            }
        }
        return downloadScope
    }

    /**
     * 暂停任务
     * 只有任务的状态为[DownloadInfo.WAITING]和[DownloadInfo.LOADING]才可以被暂停
     * 暂停任务会先暂停[DownloadInfo.WAITING]的任务而后再暂停[DownloadInfo.LOADING]的任务
     */
    fun pause(downloadGroup: DownloadGroup) {
        downloadGroup.downloadScopeList?.forEach {
            if (it.isWaiting() || it.isLoading()) {
                it.pause()
            }
        }
    }

    fun remove(downloadGroup: DownloadGroup) {
        downloadGroup.downloadScopeList?.forEach {
            if (it.isWaiting() || it.isLoading()) {
                it.remove()
            }
        }
    }

    /**
     * 暂停所有的任务
     * 只有任务的状态为[DownloadInfo.WAITING]和[DownloadInfo.LOADING]才可以被暂停
     * 暂停任务会先暂停[DownloadInfo.WAITING]的任务而后再暂停[DownloadInfo.LOADING]的任务
     */
    fun pauseAll() {
        for (entry in scopeMap) {
            val downloadScope = entry.value
            if (downloadScope.isWaiting())
                downloadScope.pause()
        }
        for (entry in scopeMap) {
            val downloadScope = entry.value
            if (downloadScope.isLoading())
                downloadScope.pause()
        }
    }

    /**
     * 移除所有的任务
     * 移除任务会先移除状态不为[DownloadInfo.LOADING]的任务
     * 而后再移除状态为[DownloadInfo.LOADING]的任务
     */
    fun removeAll() {
        for (entry in scopeMap) {
            val downloadScope = entry.value
            if (!downloadScope.isLoading())
                downloadScope.remove()
        }
        for (entry in scopeMap) {
            val downloadScope = entry.value
            if (downloadScope.isLoading())
                downloadScope.pause()
        }
    }

    /**
     * 启动下载任务
     * 请不要直接使用此方法启动下载任务,它是交由[DownloadScope]进行调用
     */
    internal fun launchScope(scope: DownloadScope) {
        if (taskScopeMap.size >= MAX_SCOPE) return
        if (taskScopeMap.contains(scope.downloadInfo.url)) return
        taskScopeMap[scope.downloadInfo.url] = scope
        scope.launch()
    }

    /**
     * 启动下一个任务,如果有正在等待中的任务的话
     * 请不要直接使用此方法启动下载任务,它是交由[DownloadScope]进行调用
     * @param previousUrl 上一个下载任务的下载连接
     */
    internal fun launchNext(previousUrl: String) {
        taskScopeMap.remove(previousUrl)
        for (entrySet in scopeMap) {
            val downloadScope = entrySet.value
            if (downloadScope.isWaiting()) {
                launchScope(downloadScope)
                break
            }
        }
    }
}