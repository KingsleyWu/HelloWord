package com.kingsley.download.utils

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Environment
import com.kingsley.common.L
import com.kingsley.download.appContext
import com.kingsley.download.base.RetrofitDownload
import com.kingsley.download.bean.DGBuilder
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.bean.DownloadGroup
import com.kingsley.download.core.DownloadTask
import com.kingsley.download.core.IDownloader
import com.kingsley.download.core.INotifier
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap

/** id key */
const val KEY_TASK_ID = "taskId"

/** error key */
const val KEY_ERROR = "error"

/** notify_id key */
const val KEY_NOTIFY_ID = "notify_id"

/** 默認類型 */
const val DEFAULT_TYPE = "default"

/** 並行下載 */
const val CONFIG_PARALLEL = 2

/** 下載需要 WIFI */
const val CONFIG_WIFI_NEED = 4

/** 下載顯示通知 */
const val CONFIG_SHOW_NOTIFICATION = 8

/** 下載時重新覆蓋文件，即 刪除原來下載的文件 */
const val CONFIG_DOWNLOAD_REPLACE = 16

/** 下载失败后，保持已下载的文件数据 */
const val CONFIG_OCCUR_ERROR_KEEP_FILE = 32

object DownloadUtils {

    /**
     * 是否需要 .download 後綴，默認為 true
     */
    @JvmField
    var needDownloadSuffix = true

    /**
     * 最大同時運行的下載數量 1
     */
    @JvmField
    var MAX_TASK = 1

    /**
     * 默認進度更新時間 500毫秒
     */
    @JvmField
    var updateTime = 500L

    /**
     * 下載器
     */
    var downloader: IDownloader = RetrofitDownload

    /**
     * 消息通知
     */
    var notifier: INotifier? = DownloadNotifier()

    /**
     * 下載的路徑
     */
    val downloadFolder: String? by lazy {
        appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
    }

    /**
     * 當前所有的 DownloadTask
     */
    private val allTaskMap = ConcurrentHashMap<String, DownloadTask>()

    /**
     * 當前運行的 DownloadTask
     */
    private val runningTaskMap = ConcurrentHashMap<String, DownloadTask>()

    /**
     * 等待中的 DownloadTask
     */
    private val pendingTaskMap = ConcurrentHashMap<String, DownloadTask>()

    /**
     * wifi 是否可用
     */
    var sWifiAvailable = NetworkUtils.isWifiAvailable(appContext)
        internal set

    /**
     * 網絡 是否可用
     */
    var sConnectivityAvailable = NetworkUtils.isNetworkAvailable(appContext)
        internal set

    /**
     * 網絡監聽
     */
    private val mConnectivityChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            changeConnectivity()
        }
    }

    /**
     * 網絡監聽
     */
    private val mNetworkChangeCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            changeConnectivity()
        }

        override fun onLost(network: Network) {
            changeConnectivity()
        }
    }

    init {
        initConnectivityChangeReceiver()
    }

    /**
     * 暫停任务
     */
    internal fun pause(id: String) {
        allTaskMap[id]?.let {
            it.update(DownloadInfo.PAUSE)
            DownloadBroadcastUtil.sendBroadcast(DownloadAction.Pause(it.data))
        }
    }

    /**
     * 等待下载,如等待网络链接
     */
    internal fun waiting(id: String) {
        allTaskMap[id]?.let {
            it.update(DownloadInfo.WAITING)
            DownloadBroadcastUtil.sendBroadcast(DownloadAction.Waiting(it.data))
        }
    }

    /**
     * 運行下一個下載
     */
    internal fun downloadNext(task: DownloadTask) {
        L.d("downloadNext ")
        val id = task.data.id
        runningTaskMap.remove(id)
        if (runningTaskMap.size < MAX_TASK && pendingTaskMap.size > 0) {
            val pendingIterator = pendingTaskMap.iterator()
            for (entrySet in pendingIterator) {
                val nextTask = entrySet.value
                if (id != entrySet.key && nextTask.data.status != DownloadInfo.DONE) {
                    if (runningTaskMap[entrySet.key] == null) {
                        runningTaskMap[entrySet.key] = nextTask
                        nextTask.data.status = DownloadInfo.NONE
                        pendingIterator.remove()
                    }
                    L.d("繼續下一個下載， 下一個 Group id = ${nextTask.data.id}， percent = ${nextTask.data.percent()}")
                    nextTask.start()
                    if (runningTaskMap.size >= MAX_TASK) {
                        L.d("到達最多允許下載數據，只允許有${MAX_TASK}個下載，當前下載中的數量為：${runningTaskMap.size}, 等待中的數量為：${pendingTaskMap.size}")
                        break
                    }
                }
            }
        } else {
            L.d("只允許有${MAX_TASK}個下載，當前下載中的數量為：${runningTaskMap.size}, 等待中的數量為：${pendingTaskMap.size}")
        }
    }

    /**
     * 通過 data 獲取 DownloadTask，當沒有獲取到時會進行創建，
     * 如沒有則創建並加入 db
     */
    @JvmStatic
    fun request(builder: DGBuilder): DownloadTask {
        return request(builder.build())
    }

    /**
     * 通過 id 從 taskMap 中或 db 中獲取 DownloadTask，當沒有時不會進行創建
     * DownloadGroup 可以通過 [DGBuilder] 進行創建
     */
    @JvmStatic
    fun request(id: String): DownloadTask? {
        return getTaskFromMapOrDb(id)
    }

    /**
     * 通過 data 獲取 DownloadTask，當沒有獲取到時會進行創建，
     * 如沒有則創建並加入 db
     * DownloadGroup 可以通過 [DGBuilder] 進行創建
     */
    @JvmStatic
    fun request(data: DownloadGroup): DownloadTask {
        return getTaskFromMapOrDb(data.id) ?: createDownloadTask(data)
    }

    /**
     * 從 taskMap 中獲取或 db 中獲取
     */
    private fun getTaskFromMapOrDb(id: String): DownloadTask? {
        return allTaskMap[id] ?: getTaskFromDb(id)
    }

    /**
     * 從 db 中獲取
     */
    private fun getTaskFromDb(id: String): DownloadTask? {
        return DownloadDBUtil.queryById(id)?.let {
            return createDownloadTask(it)
        }
    }

    /**
     * 創建並加入 db
     */
    private fun createDownloadTask(data: DownloadGroup): DownloadTask {
        return DownloadTask(data).also {
            allTaskMap[data.id] = it
        }
    }

    /**
     * 下載
     * 如需下需要通過 request() 方法獲取 DownloadTask，然後通過 DownloadTask.download() 進行下載
     */
    internal fun download(task: DownloadTask) {
        var downloadTask = runningTaskMap[task.data.id]
        if (downloadTask != null) {
            if (!downloadTask.isDownloading()) {
                L.d("繼續下載， 當前 Group id = ${task.data.id}， percent = ${task.data.percent()}")
                downloadTask.start()
            }
        } else {
            if (task.data.status != DownloadInfo.DONE) {
                downloadTask = task
                if (runningTaskMap.size < MAX_TASK) {
                    runningTaskMap[task.data.id] = downloadTask
                    if (!downloadTask.isDownloading()) {
                        L.d("加入到下載， 當前 Group id = ${task.data.id}， percent = ${task.data.percent()}")
                        downloadTask.start()
                    }
                } else {
                    L.d("加入到等待隊列， 當前 Group id = ${task.data.id}， percent = ${task.data.percent()}")
                    if (!downloadTask.isPending()) {
                        downloadTask.pending()
                    }
                    pendingTaskMap[task.data.id] = downloadTask
                }
            } else {
                L.d("已經下載完成了， 當前 Group 狀態 status : ${task.data.status}， id = ${task.data.id}， percent = ${task.data.percent()}")
                task.update(DownloadInfo.DONE)
            }
        }
    }

    /**
     * 通過單個 url 獲取 DownloadTask，當沒有獲取到時會進行創建，
     * 如沒有則創建並加入 db
     */
    @JvmStatic
    fun request(
        url: String,
        type: String = DEFAULT_TYPE,
        action: String? = "",
        title: String = "",
        fileType: String = "",
        flag: String = "",
        path: String = "",
        fileName: String? = null
    ): DownloadTask {
        val id = url.hashCode().toString()
        return getTaskFromMapOrDb(id) ?: createDownloadTask(
            DownloadGroup(
                id = url.hashCode().toString(),
                type = type,
                request = DownloadInfo(
                    url = url,
                    notifyClickUri = action,
                    notifyTitle = title,
                    fileType = fileType,
                    flag = flag,
                    path = path,
                    fileName = fileName
                )
            )
        )
    }

    /**
     * 通過單個 url 獲取 DownloadTask，當沒有獲取到時會進行創建，
     * 如沒有則創建並加入 db
     */
    @JvmStatic
    fun request(
        url: String,
        type: String = DEFAULT_TYPE,
        dirName: String? = "",
        childBuilder: DGBuilder.(MutableList<DownloadInfo>) -> Unit
    ): DownloadTask {
        val id = url.hashCode().toString()
        return getTaskFromMapOrDb(id) ?: createDownloadTask(
            DGBuilder().id(id).type(type).dirName(dirName).also {
                val downloadList = mutableListOf<DownloadInfo>()
                childBuilder(it, downloadList)
                if (downloadList.isNotEmpty()) {
                    for (downloadRequest in downloadList) {
                        it.addChild(downloadRequest)
                    }
                }
            }.build()
        )
    }

    internal fun doAction(task: DownloadTask) {
        when {
            task.isDownloading() -> pause(task.data.id)
            task.canDownload() -> download(task)
        }
    }


    /* 網絡監聽 start */

    @Suppress("DEPRECATION")
    @SuppressLint("ObsoleteSdkInt")
    private fun initConnectivityChangeReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val connectivityManager =
                appContext.getSystemService(Service.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager?.registerDefaultNetworkCallback(mNetworkChangeCallback)
            } else {
                val builder = NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                    .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                connectivityManager?.registerNetworkCallback(
                    builder.build(),
                    mNetworkChangeCallback
                )
            }
        } else {
            // 注册网络变化监听
            appContext.registerReceiver(
                mConnectivityChangeReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    private fun changeConnectivity() {
        try {
            NetworkUtils.getNetworkAvailable(appContext).also {
                sWifiAvailable = it.first
                sConnectivityAvailable = it.second
            }
        } catch (e: Exception) {
            L.e(e)
        } finally {
            performNetworkAction()
        }
    }

    private fun performNetworkAction() {
        L.e("performNetworkAction sConnectivityAvailable = $sConnectivityAvailable, sWifiAvailable = $sWifiAvailable ")
        if (runningTaskMap.isNotEmpty()) {
            for (groupTask in runningTaskMap.values) {
                if (!groupTask.isDone() && !groupTask.isPaused()) {
                    groupTask.performNetworkAction()
                }
            }
        }
    }

    /* 網絡監聽 end */
}