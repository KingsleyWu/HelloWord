package com.kingsley.download.core

import android.os.Environment
import android.text.TextUtils
import com.kingsley.common.L
import com.kingsley.download.appContext
import com.kingsley.download.base.RetrofitDownload
import com.kingsley.download.bean.DownloadGroup
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.bean.Result
import com.kingsley.download.client.RoomClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.*

object DownloadUtils {

    var needDownloadSuffix = true

    /**
     * 下載的路徑
     */
    private val downloadFolder: String? by lazy {
        appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
    }

    /**
     * 下載器
     */
    var downloader: IDownloader = RetrofitDownload

    /**
     * 文件下载
     * @param downloadInfo 下载的信息
     * @return Flow<DownloadStatus> 當前下載的狀態
     */
    private fun download(downloadGroup: DownloadGroup, downloadInfo: DownloadInfo?): Flow<DownloadStatus> {
        return flow {
            when {
                downloadInfo == null -> throw IllegalArgumentException("DownloadInfo is null")
                //发送下载完成通知
                downloadGroup.isDone() -> emit(DownloadStatus.Done(downloadInfo))
                else -> {
                    val startPosition = downloadInfo.currentLength
                    //验证断点有效性
                    if (startPosition < 0) throw IllegalArgumentException("Start position less than zero")
                    //下载的文件是否已经被删除
                    if (startPosition > 0 && !TextUtils.isEmpty(downloadInfo.path)) {
                        val filePath = "${downloadInfo.path}${if (needDownloadSuffix) ".download" else ""}"
                        if (!File(filePath).exists()) throw FileNotFoundException("File does not exist, $filePath file has been deleted!")
                    }
                    downloadInfo.status = DownloadInfo.WAITING
                    RoomClient.dataBase.downloadDao().insertOrReplace(downloadInfo)
                    emit(DownloadStatus.Waiting(downloadInfo))
                    L.d("wwc startPosition = $startPosition")
                    val result = downloader.download(
                        start = "bytes=$startPosition-",
                        url = downloadInfo.url
                    )
                    when (result) {
                        is Result.Success -> {
                            val total = result.contentLength
                            downloadInfo.contentLength = total
                            //保存的文件名称
                            if (TextUtils.isEmpty(downloadInfo.fileName)) {
                                downloadInfo.fileName = UrlUtils.getUrlFileName(downloadInfo.url)
                            }
                            //创建File,如果已经指定文件path,将会使用指定的path,如果没有指定将会使用默认的下载目录
                            val file: File
                            val tempFile: File
                            if (TextUtils.isEmpty(downloadInfo.path)) {
                                val fileName =
                                    "${downloadInfo.fileName!!}${if (needDownloadSuffix) ".download" else ""}"
                                file = File(downloadFolder, downloadInfo.fileName!!)
                                tempFile = File(downloadFolder, fileName)
                                downloadInfo.path = file.absolutePath
                            } else {
                                val filePath =
                                    "${downloadInfo.path!!}${if (needDownloadSuffix) ".download" else ""}"
                                file = File(downloadInfo.path!!)
                                tempFile = File(filePath)
                            }
                            //再次验证下载的文件是否已经被删除
                            if (startPosition > 0 && !file.exists() && !tempFile.exists()) throw FileNotFoundException("File does not exist")
                            //再次验证断点有效性
                            if (startPosition > downloadInfo.contentLength) throw IllegalArgumentException("Start position greater than content length")
                            //验证下载完成的任务与实际文件的匹配度
                            if (startPosition == downloadInfo.contentLength && startPosition > 0) {
                                if ((file.exists() || tempFile.exists()) && startPosition == file.length()) {
                                    downloadInfo.status = DownloadInfo.DONE
                                    RoomClient.dataBase.downloadDao().insertOrReplace(downloadInfo)
                                    downloadInfo.renameFile()
                                    //发送下载完成通知
                                    emit(DownloadStatus.Done(downloadInfo))
                                } else {
                                    throw IOException("The content length is not the same as the file length")
                                }
                            } else {
                                //写入文件
                                val randomAccessFile = RandomAccessFile(tempFile, "rw")
                                randomAccessFile.seek(startPosition)
                                //文件读写
                                randomAccessFile.use { output ->
                                    val input = result.byteStream
                                    var emittedProgress = 0L
                                    //使用对应的扩展函数 ，因为该函数最后参为内联函数，因此需要在后面实现对应业务逻辑
                                    input.copyTo(output, startPosition) { bytesCopied ->
                                        //获取下载进度百分比
                                        val progress = bytesCopied * 100 / total
                                        //每下载进度比上次大于5时，通知UI线程
                                        if (progress - emittedProgress > 5) {
                                            delay(100)
                                            val currentTime = System.currentTimeMillis()
                                            downloadInfo.lastRefreshTime = currentTime
                                            downloadInfo.currentLength = bytesCopied
                                            downloadInfo.status = DownloadInfo.LOADING
                                            RoomClient.dataBase.downloadDao().insertOrReplace(downloadInfo)
                                            //使用Flow对应的emit 发送对应下载进度通知
                                            emit(
                                                DownloadStatus.Loading(
                                                    downloadInfo,
                                                    progress.toInt()
                                                )
                                            )
                                            //记录当前下载进度
                                            emittedProgress = progress
                                        }
                                    }
                                }
                                downloadInfo.status = DownloadInfo.DONE
                                downloadInfo.currentLength = total
                                RoomClient.dataBase.downloadDao().insertOrReplace(downloadInfo)
                                downloadInfo.renameFile()
                                if (TextUtils.isEmpty(downloadInfo.childUrl)) {
                                    //发送下载完成通知
                                    emit(DownloadStatus.Done(downloadInfo))
                                } else {
                                    val childDownloadInfo = downloadGroup.next(downloadInfo)
                                    L.d("wwc downloadInfo = " + downloadInfo + " , childDownloadInfo.url = " + childDownloadInfo?.url)
                                    if (childDownloadInfo != null) {
                                        download(downloadGroup, childDownloadInfo).collect {
                                            emit(it)
                                        }
                                    }
                                }
                            }
                        }
                        is Result.Error -> {
                            throw IOException(result.message)
                        }
                    }
                }
            }
        }.catch {
            it.printStackTrace()
            if (downloadInfo != null) {
                downloadInfo.status = DownloadInfo.ERROR
                downloadInfo.message = it.message
                RoomClient.dataBase.downloadDao().insertOrReplace(downloadInfo)
            }
            //下载失败，发送失败通知
            emit(DownloadStatus.Error(it))
            //因为下载文件是属于异步IO操作，因此这里改变上下文
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 文件下载
     * @param downloadGroup 下载的信息
     * @return Flow<DownloadStatus> 當前下載的狀態
     */
    fun download(downloadGroup: DownloadGroup): Flow<DownloadStatus> {
        return download(downloadGroup, downloadGroup.first())
    }

}

/**
 * 下載狀態
 */
sealed class DownloadStatus {
    /** 空状态 */
    object None : DownloadStatus()

    /**
     * 等待中
     * @param downloadInfo 下載的信息
     */
    data class Waiting(val downloadInfo: DownloadInfo) : DownloadStatus()

    /**
     * 下载中
     * @param downloadInfo 下載的信息
     * @param progress 進度
     */
    data class Loading(val downloadInfo: DownloadInfo, val progress: Int) : DownloadStatus()

    /**
     * 暫停
     * 下载中
     * @param downloadInfo 下載的信息
     */
    data class Pause(val downloadInfo: DownloadInfo) : DownloadStatus()

    /** 错误
     * @param throwable 下载錯誤的信息
     */
    data class Error(val throwable: Throwable) : DownloadStatus()

    /**
     * 完成
     * @param downloadInfo 下載的信息
     */
    data class Done(val downloadInfo: DownloadInfo) : DownloadStatus()
}

private fun DownloadInfo.renameFile() {
    if (status == DownloadInfo.DONE && DownloadUtils.needDownloadSuffix) {
        path?.let {
            val file = File(it)
            val tempFile = File("$it.download")
            if (!file.exists() && tempFile.exists()) {
                tempFile.renameTo(file)
            }
        }
    }
}

/**
 * @param out: OutputStream 这个不用多说，下载保存的对象流
 * @param startPosition: Int 開始下載的位置，可不传
 * @param bufferSize: Int = DEFAULT_BUFFER_SIZE，参数值默认为：DEFAULT_BUFFER_SIZE，可不传
 * @param progress: (Long)-> Unit 進度回調，内联函数，参数为Long，返回值为Null
 */
private inline fun InputStream.copyTo(
    out: RandomAccessFile,
    startPosition: Long = 0,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    progress: ((Long) -> Unit)
): Long {
    var bytesCopied = startPosition
    val buffer = ByteArray(bufferSize)
    var bytes: Int
    while (read(buffer).also { bytes = it } >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        //在最后调用内联函数
        progress(bytesCopied)
    }
    return bytesCopied
}