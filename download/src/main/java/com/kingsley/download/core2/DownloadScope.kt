package com.kingsley.download.core2

import android.text.TextUtils
import com.kingsley.common.L
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.bean.Result
import com.kingsley.download.client.RoomClient
import com.kingsley.download.core.UrlUtils
import kotlinx.coroutines.*
import java.io.*
import java.util.concurrent.CancellationException
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 代表一个下载任务
 * 不要直接在外部直接创建此对象,那样就可能无法同一管理下载任务,请通过[DownloadUtils.request]获取此对象
 *
 * @param downloadInfo 下載的信息
 */
class DownloadScope(
    var downloadInfo: DownloadInfo,
    var changeListener: ((DownloadInfo) -> Unit)? = null
) : CoroutineScope by CoroutineScope(EmptyCoroutineContext) {

    private var downloadJob: Job? = null

    init {
        L.d("wwc downloadInfo.group = ${downloadInfo.group}")
        launch(Dispatchers.Main) {
            val localInfoDeferred = async(Dispatchers.IO) {
                RoomClient.dataBase.downloadDao().queryByUrl(downloadInfo.url)
            }
            val localInfo = localInfoDeferred.await()
            //数据库中并没有任务,这是一个新的下载任务
            if (localInfo != null) {
                if (TextUtils.isEmpty(downloadInfo.path) && !TextUtils.isEmpty(localInfo.path)) {
                    downloadInfo.path = localInfo.path
                }
                if (TextUtils.isEmpty(downloadInfo.fileName) && !TextUtils.isEmpty(localInfo.fileName)) {
                    downloadInfo.fileName = localInfo.fileName
                }
                if (downloadInfo.data == null && localInfo.data != null) {
                    downloadInfo.data = localInfo.data
                }
                downloadInfo.contentLength = localInfo.contentLength
                downloadInfo.currentLength = localInfo.currentLength
            }
            //将原本正在下载中的任务恢复到暂停状态,防止意外退出出现的状态错误
            if (downloadInfo.status == DownloadInfo.LOADING) {
                downloadInfo.status = DownloadInfo.PAUSE
            }
            changeListener?.invoke(downloadInfo)
        }
    }

    /**
     * 开始任务的下载
     * [DownloadInfo]是在协程中进行创建的,它的创建会优先从数据库中获取,但这种操作是异步的,详情请看init代码块
     * 我们需要通过观察者观察[DownloadInfo]来得知它是否已经创建完成,只有当他创建完成且不为空(如果创建完成,它一定不为空)
     * 才可以交由[DownloadUtils]进行下载任务的启动
     * 任务的开始可能并不是立即的,任务会受到[DownloadUtils]的管理
     */
    fun start() {
        if (canDownload()) {
            change(DownloadInfo.WAITING)
            DownloadUtils.launchScope(this)
        }
    }

    /**
     * 启动协程进行下载
     * 请不要尝试在外部调用此方法,那样会脱离[DownloadUtils]的管理
     */
    internal fun launch() = launch(Dispatchers.Main) {
        L.d("wwc launch")
        var error: Throwable? = null
        val childUrl = downloadInfo.childUrl
        try {
            if (downloadInfo.status != DownloadInfo.DONE) {
                download()
            }
            change(DownloadInfo.DONE)
        } catch (e: Throwable) {
            error = e
            L.e(
                "DownloadScope",
                "launch error:${e.message} , url = ${downloadInfo.url}, childUrl = $childUrl"
            )
            when (e) {
                !is CancellationException -> change(DownloadInfo.ERROR, e.message)
            }
        } finally {
            L.d("DownloadScope", "launch finally error = $error , childUrl = $childUrl")
        }
    }.also { downloadJob = it }

    private fun download() = launch(Dispatchers.IO){
        change(DownloadInfo.LOADING)
        val startPosition = downloadInfo.currentLength
        //验证断点有效性
        if (startPosition < 0) throw IllegalArgumentException("Start position less than zero")
        //下载的文件是否已经被删除
        if (startPosition > 0 && !TextUtils.isEmpty(downloadInfo.path)) {
            val filePath =
                "${downloadInfo.path!!}${if (DownloadUtils.needDownloadSuffix) ".download" else ""}"
            if (!File(filePath).exists()) throw FileNotFoundException("File does not exist, $filePath file has been deleted!")
        }
        val result = DownloadUtils.downloader.download(
            start = "bytes=$startPosition-",
            url = downloadInfo.url
        )
        if (result is Result.Success) {
            //文件长度
            downloadInfo.contentLength = result.contentLength
            //保存的文件名称
            if (TextUtils.isEmpty(downloadInfo.fileName)) {
                downloadInfo.fileName = UrlUtils.getUrlFileName(downloadInfo.url)
            }
            //创建File,如果已经指定文件path,将会使用指定的path,如果没有指定将会使用默认的下载目录
            val file: File
            val tempFile: File
            if (TextUtils.isEmpty(downloadInfo.path)) {
                val fileName =
                    "${downloadInfo.fileName!!}${if (DownloadUtils.needDownloadSuffix) ".download" else ""}"
                file = File(DownloadUtils.downloadFolder, downloadInfo.fileName!!)
                tempFile = File(DownloadUtils.downloadFolder, fileName)
                downloadInfo.path = file.absolutePath
            } else {
                val filePath =
                    "${downloadInfo.path!!}${if (DownloadUtils.needDownloadSuffix) ".download" else ""}"
                file = File(downloadInfo.path!!)
                tempFile = File(filePath)
            }
            //再次验证下载的文件是否已经被删除
            if (startPosition > 0 && !file.exists() && !tempFile.exists()) throw FileNotFoundException(
                "File does not exist"
            )
            //再次验证断点有效性
            if (startPosition > downloadInfo.contentLength) throw IllegalArgumentException("Start position greater than content length")
            //验证下载完成的任务与实际文件的匹配度
            if (startPosition == downloadInfo.contentLength && startPosition > 0) {
                if ((file.exists() || tempFile.exists()) && startPosition == file.length()) {
                    change(DownloadInfo.DONE)
                    return@launch
                } else throw IOException("The content length is not the same as the file length")
            }
            copy(tempFile, result.byteStream) { bytesCopied ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - downloadInfo.lastRefreshTime > 300) {
                    downloadInfo.lastRefreshTime = currentTime
                    downloadInfo.currentLength = bytesCopied
                    change(DownloadInfo.LOADING)
                }
            }
            downloadInfo.currentLength = downloadInfo.contentLength
            change(DownloadInfo.DONE)
        } else {
            if (result is Result.Error) {
                change(DownloadInfo.ERROR, result.message)
            }
        }
    }

    private inline fun copy(file: File, input: InputStream, progress: ((Long) -> Unit)) {
        //写入文件
        val randomAccessFile = RandomAccessFile(file, "rw")
        randomAccessFile.seek(downloadInfo.currentLength)
        randomAccessFile.use { output ->
            BufferedInputStream(input, DEFAULT_BUFFER_SIZE).use {
                //使用对应的扩展函数 ，因为该函数最后参为内联函数，因此需要在后面实现对应业务逻辑
                it.copyTo(
                    output,
                    downloadInfo.currentLength,
                    isActive = isActive,
                    progress = progress
                )
            }
        }
    }

    /**
     * 更新任务
     * @param status [DownloadInfo.Status]
     */
    private fun change(status: Int, message: String? = "") = launch(Dispatchers.Main) {
        downloadInfo.status = status
        downloadInfo.message = message
        withContext(Dispatchers.IO) {
            RoomClient.dataBase.downloadDao().insertOrReplace(downloadInfo)
            if (status == DownloadInfo.DONE && DownloadUtils.needDownloadSuffix) {
                downloadInfo.path?.let { path ->
                    val file = File(path)
                    val tempFile = File("$path.download")
                    if (!file.exists() && tempFile.exists()) {
                        tempFile.renameTo(file)
                    }
                }
            }
        }
        changeListener?.invoke(downloadInfo)
    }

    /**
     * 暂停任务
     * 只有等待中的任务和正在下载中的任务才可以进行暂停操作
     */
    fun pause() {
        cancel(CancellationException("pause"))
        if (downloadInfo.status == DownloadInfo.LOADING || downloadInfo.status == DownloadInfo.WAITING) {
            change(DownloadInfo.PAUSE)
        }
    }

    /**
     * 删除任务,删除任务会同时删除已经在数据库中保存的下载信息
     */
    fun remove() {
        cancel(CancellationException("remove"))
        downloadInfo.reset()
        change(DownloadInfo.NONE)
        launch(Dispatchers.IO) {
            RoomClient.dataBase.downloadDao().delete(downloadInfo)
            //同时删除已下载的文件
            downloadInfo.path?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
                val tempFile = File("$path.download")
                if (tempFile.exists()) {
                    tempFile.delete()
                }
            }
        }
    }

    /**
     * 取消[downloadJob],将会中断正在进行的下载任务
     */
    private fun cancel(cause: CancellationException) {
        downloadJob?.cancel(cause)
    }

    /**
     * 获取[DownloadInfo]
     */
    fun downloadInfo() = downloadInfo

    /**
     * 是否是等待任务
     */
    fun isWaiting() = downloadInfo.status == DownloadInfo.WAITING

    /**
     * 是否是正在下载的任务
     */
    fun isLoading() = downloadInfo.status == DownloadInfo.LOADING

    /**
     * 是否可以下載
     */
    fun canDownload(): Boolean {
        val status = downloadInfo.status
        return status == DownloadInfo.PAUSE || status == DownloadInfo.ERROR || status == DownloadInfo.NONE || status == DownloadInfo.WAITING
    }
}

/**
 * @param out: OutputStream 这个不用多说，下载保存的对象流
 * @param startPosition: Int 開始下載的位置，可不传
 * @param bufferSize: Int = DEFAULT_BUFFER_SIZE，参数值默认为：DEFAULT_BUFFER_SIZE，可不传
 * @param progress: (Long)-> Unit 進度回調，内联函数，参数为Long，返回值为Null
 */
internal inline fun InputStream.copyTo(
    out: RandomAccessFile,
    startPosition: Long = 0,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    isActive: Boolean,
    progress: ((Long) -> Unit)
): Long {
    var bytesCopied = startPosition
    val buffer = ByteArray(bufferSize)
    var bytes = 0
    BufferedInputStream(this, bufferSize).read(buffer)
    while (isActive && read(buffer).also { bytes = it } >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        //在最后调用内联函数
        progress(bytesCopied)
    }
    return bytesCopied
}
