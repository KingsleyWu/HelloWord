package com.kingsley.download.core

import android.text.TextUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kingsley.common.L
import com.kingsley.download.bean.DownloadResult
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.bean.DownloadGroup
import com.kingsley.download.utils.DownloadBroadcastUtil
import com.kingsley.download.utils.DownloadDBUtil
import com.kingsley.download.utils.DownloadUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

class DownloadTask(val data: DownloadGroup) {
    /** 下载的协程作用域 */
    var downloadScope : CoroutineScope = DownloadScope

    /** 当前下载的 job */
    private var job: Job? = null

    /** 监听器 私有 */
    private val _liveData = MutableLiveData<DownloadGroup>()

    /** 监听器 */
    val liveData: LiveData<DownloadGroup> = _liveData

    internal fun start() {
        if (job == null || !job!!.isActive) {
            job = downloadScope.launch {
                download(data).collect {
                    withContext(Dispatchers.Main) {
                        // 结果
                        _liveData.value = it
                        // 创建通知
                        DownloadUtils.notifier?.createNotification(it)
                        when (it.status) {
                            DownloadInfo.DONE, DownloadInfo.WAITING -> DownloadBroadcastUtil.sendBroadcast(it)
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加下载任务观察者並下载
     * @param lifecycleOwner lifecycleOwner
     * @param needRemoveObservers needRemoveObservers
     * @param observer observer
     */
    fun download(lifecycleOwner: LifecycleOwner, needRemoveObservers: Boolean = true, observer: Observer<DownloadGroup>) {
        DownloadUtils.download(observer(lifecycleOwner, needRemoveObservers, observer))
    }

    /**
     * 下載
     */
    internal fun download(data: DownloadGroup): Flow<DownloadGroup> {
        return flow {
            // 初始化請求內容
            initRequest(data)
            // 如果沒有開始下載過就直接設置為開始,其他狀態則表示已經開始下載過了
            if (data.status != DownloadInfo.DONE) {
                emit(data.update(DownloadInfo.WAITING))
            }
            // 判斷 url 是否存在重複
            if (data.validate()) {
                // 下載的 url 或 重定向的url 存在重複內容
                emit(data)
                return@flow
            }
            // 是否是存在重定向 url
            var isRedirect = false
            // 初始化一下內容 contentLength
            if (data.totalLength < 0) {
                var temp = data.request
                while (temp != null) {
                    val request = DownloadUtils.downloader.redirect(temp.url)
                    if (request is DownloadResult.Redirect) {
                        // 獲取內容長度 contentLength
                        temp.contentLength = request.contentLength
                        // 獲取總長度
                        data.totalLength += request.contentLength
                        // 如 newUrl 與 url 不一致 ，則複值 redirectUrl，表示這個是重定向鏈接
                        if (temp.url != request.newUrl) {
                            temp.redirectUrl = request.newUrl
                            // "獲取內容長度，存在重定向 url = ${temp.url}， redirectUrl = ${temp.redirectUrl}"
                            isRedirect = true
                        }
                    } else if (request is DownloadResult.Error) {
                        // 出現錯誤則直接報錯，讓用戶重試
                        // "獲取內容長度失敗， url = ${temp.url}"
                        emit(data.update(DownloadInfo.FAILED, request.message))
                        return@flow
                    }
                    temp = temp.child
                }
            }

            // "獲取內容長度結束"
            // "第二次判斷 url 或 重定向的url 是否存在重複內容"
            // 如果已經重新重定向過，則需要重新判斷 url 是否存在重複
            if (isRedirect && data.validate()) {
                // "下載的 url 或 重定向的url 存在重複內容 $data"
                emit(data)
                return@flow
            }
            // "開始下載"
            var temp = data.request
            while (temp != null) {
                temp.update(status = DownloadInfo.DOWNLOADING)
                //保存的文件名称
                if (TextUtils.isEmpty(temp.fileName)) {
                    temp.fileName = UrlUtils.getUrlFileName(temp.url)
                }
                //创建File,如果已经指定文件path,将会使用指定的path,如果没有指定将会使用默认的下载目录
                val file: File
                val tempFile: File
                if (TextUtils.isEmpty(temp.path)) {
                    val fileName =
                        "${temp.fileName!!}${if (DownloadUtils.needDownloadSuffix) ".download" else ""}"
                    val dirName =
                        DownloadUtils.downloadFolder + (if (data.dirName.isNullOrEmpty()) "" else "${File.separator}${data.dirName}")
                    file = File(dirName, temp.fileName!!)
                    tempFile = File(dirName, fileName)
                    temp.path = file.absolutePath
                } else {
                    val filePath =
                        "${temp.path}${if (DownloadUtils.needDownloadSuffix) ".download" else ""}"
                    file = File(temp.path)
                    tempFile = File(filePath)
                }
                if (
                    (file.exists() || tempFile.exists())
                    && temp.contentLength == file.length()
                    && (temp.status == DownloadInfo.DONE || ((temp.currentLength > 0 || temp.currentLength == temp.contentLength)))
                ) {
                    L.d("data download : DONE, status = ${temp.status}, isActive = ${currentCoroutineContext().isActive}")
                    temp.update(DownloadInfo.DONE)
                    rename(temp)
                } else {
                    val url = temp.redirectUrl.ifEmpty { temp!!.url }
                    val response = DownloadUtils.downloader.download(
                        start = "bytes=${temp.currentLength}-",
                        url = url
                    )
                    if (response is DownloadResult.Success) {
                        //文件长度
                        if (temp.contentLength < 0) {
                            temp.contentLength = response.contentLength
                        }
                        //写入文件
                        val randomAccessFile = RandomAccessFile(tempFile, "rw")
                        randomAccessFile.seek(temp.currentLength)
                        val inputStream = response.byteStream
                        val bufferSize = 1024 * 8
                        val buffer = ByteArray(bufferSize)
                        val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
                        var readLength: Int
                        try {
                            L.d("data download : DOWNLOADING, status = ${temp.status}, isActive = ${currentCoroutineContext().isActive}")
                            //isActive保证任务能被及时取消
                            while (
                                bufferedInputStream.read(buffer, 0, bufferSize)
                                    .also { readLength = it } != -1
                                && temp.status == DownloadInfo.DOWNLOADING
                                && currentCoroutineContext().isActive
                            ) {
                                randomAccessFile.write(buffer, 0, readLength)
                                temp.currentLength += readLength
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - temp.updateTime > DownloadUtils.updateTime) {
                                    temp.update(DownloadInfo.DOWNLOADING)
                                    data.update(DownloadInfo.DOWNLOADING)
                                    L.d("data download : DOWNLOADING")
                                    emit(data)
                                }
                            }
                        } finally {
                            inputStream.close()
                            randomAccessFile.close()
                            bufferedInputStream.close()
                        }
                    } else if (response is DownloadResult.Error) {
                        L.d("data download : Error")
                        throw IOException(response.message)
                    }
                    if (job?.isCancelled == false) {
                        L.d("data download : DONE")
                        temp.currentLength = temp.contentLength
                        temp.update(DownloadInfo.DONE)
                        rename(temp)
                    }
                }
                temp = temp.child
            }
            if (job?.isCancelled == false) {
                L.d("data download : ALL DONE")
                data.update(DownloadInfo.DONE)
                emit(data)
            }
        }
            .flowOn(Dispatchers.IO)
            .cancellable()
            .catch {
                it.printStackTrace()
                L.d("data download : FAILED, status = ${data.status}, message = ${it}, isActive = ${currentCoroutineContext().isActive}")
                emit(data.update(DownloadInfo.FAILED, it.message))
            }.onEach {
                L.d("data status = ${data.status}, detail : $it")
                try {
                    DownloadDBUtil.insertOrReplace(it)
                } catch (e: Exception) {
                    it.update(DownloadInfo.FAILED, e.message)
                    L.e("插入 db 異常 : ${it.status}， id = ${it.id}， message = ${e.message}")
                }
            }.onCompletion {
                DownloadUtils.downloadNext(this@DownloadTask)
            }
    }

    private fun initRequest(data: DownloadGroup) {
        var temp = data.request
        while (temp != null) {
            //保存的文件名称
            if (TextUtils.isEmpty(temp.fileName)) {
                temp.fileName = UrlUtils.getUrlFileName(temp.url)
            }
            val file: File
            if (TextUtils.isEmpty(temp.path)) {
                val dirName =
                    DownloadUtils.downloadFolder + (if (data.dirName.isNullOrEmpty()) "" else "${File.separator}${data.dirName}")
                file = File(dirName, temp.fileName!!)
                temp.path = file.absolutePath
            }
            //exits downloaded file
            var len: Long
            if (isDownloading(temp.path, DownloadUtils.needDownloadSuffix).also { len = it } > 0) {
                if (temp.currentLength != len) {
                    temp.currentLength = len
                }
            }
            if (isDownloaded(temp.path).also { len = it } > 0) {
                temp.currentLength = len
            }
            temp = temp.child
        }
    }

    private fun isDownloaded(path: String) = isDownloading(path, false)

    private fun isDownloading(path: String, needDownloadSuffix: Boolean): Long {
        val filePath = "${path}${if (needDownloadSuffix) ".download" else ""}"
        val targetFile = File(filePath)
        var length = 0L
        // file existed
        if (targetFile.exists()) {
            length = targetFile.length()
        }
        return length
    }

    /**
     * 重命名文件
     */
    private fun rename(data: DownloadInfo) {
        if (DownloadUtils.needDownloadSuffix) {
            data.path.let { path ->
                val file = File(path)
                val tempFile = File("$path.download")
                if (!file.exists() && tempFile.exists()) {
                    tempFile.renameTo(file)
                }
            }
        }
    }

    /**
     * 添加下载任务观察者
     * @param lifecycleOwner lifecycleOwner
     * @param needRemoveObservers needRemoveObservers
     * @param observer observer
     */
    fun observer(lifecycleOwner: LifecycleOwner, needRemoveObservers: Boolean = true, observer: Observer<DownloadGroup>): DownloadTask {
        downloadScope.launch(Dispatchers.Main) {
            if (needRemoveObservers) {
                _liveData.removeObservers(lifecycleOwner)
            }
            _liveData.observe(lifecycleOwner, observer)
        }
        return this
    }

    /**
     * 等待下载
     */
    internal fun pending() {
        update(DownloadInfo.PENDING)
    }

    /**
     * 取消下载
     */
    fun cancel(needCallback: Boolean = true) {
        job?.cancel()
        L.d("取消下载， 當前 Group id = ${data.id}， percent = ${data.percent()}")
        data.let {
            downloadScope.launch(Dispatchers.IO) {
                it.reset()
                DownloadDBUtil.delete(it)
                if (needCallback) {
                    withContext(Dispatchers.Main) {
                        _liveData.value = it
                        // 创建通知
                        DownloadUtils.notifier?.createNotification(it)
                    }
                }
                //同时删除已下载的文件
                it.request?.deleteFile()
            }
        }
    }

    /**
     * 更新状态
     */
    internal fun update(status: Int) {
        job?.cancel()
        L.d("update, status = $status, 當前 Group id = ${data.id}， percent = ${data.percent()}")
        data.let {
            downloadScope.launch(Dispatchers.IO) {
                it.update(status)
                DownloadDBUtil.insertOrReplace(it)
                withContext(Dispatchers.Main) {
                    _liveData.value = it
                    // 创建通知
                    DownloadUtils.notifier?.createNotification(it)
                }
            }
        }
    }
    /**
     * 下载
     */
    fun download() {
        DownloadUtils.download(this)
    }

    /**
     * 暫停下载
     */
    fun pause() {
        L.d("暫停下载， 當前 Group id = ${data.id}， percent = ${data.percent()}")
        DownloadUtils.pause(data.id)
    }

    fun isDone(): Boolean {
        return data.status == DownloadInfo.DONE
    }

    fun isPaused(): Boolean {
        return data.status == DownloadInfo.PAUSE || data.status == DownloadInfo.PAUSED
    }

    fun isDownloading(): Boolean {
        return data.status == DownloadInfo.DOWNLOADING
    }

    fun isPending(): Boolean {
        return data.status == DownloadInfo.PENDING
    }

    fun isNone(): Boolean {
        return data.status == DownloadInfo.NONE
    }

    fun isError(): Boolean {
        return data.status == DownloadInfo.FAILED
    }

    fun canDownload(): Boolean {
        return isError() || isPaused() || isNone()
    }

    /**
     * 網絡變動時的處理
     */
    internal fun performNetworkAction() {
        if (data.needWifi() && DownloadUtils.sWifiAvailable) {
            if (data.status != DownloadInfo.DONE && data.status == DownloadInfo.WAITING) {
                download()
            }
        } else if (!data.needWifi() && DownloadUtils.sConnectivityAvailable && data.status != DownloadInfo.DONE && data.status == DownloadInfo.WAITING) {
            download()
        } else if (data.status != DownloadInfo.DONE) {
            DownloadUtils.waiting(data.id)
        }
    }

    fun doAction() {
        DownloadUtils.doAction(this)
    }
}