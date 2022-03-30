package com.kingsley.net.download

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.kingsley.common.L
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.bean.Result
import com.kingsley.download.client.RoomClient
import com.kingsley.download.core.UrlUtils
import com.kingsley.download.core2.DownloadUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.*

class DownloadTask(
    var coroutineScope: CoroutineScope, // 协程作用域
    var liveData: MutableLiveData<DownloadInfo>? = null, // 监听器
    var url: String,
    var action: String? = null,
    var type: String? = null,
    var flag: String? = null,
    var path: String? = null,
    var fileName: String? = null,
    var group: String? = null,
    var childUrl: String? = null,
    private val data: Serializable? = null
) {

    private var download: Job? = null
    var downloadInfo: DownloadInfo? = null

    init {
        coroutineScope.launch(Dispatchers.IO) {
            // 下载前准备
            downloadInfo = RoomClient.dataBase.downloadDao().queryByUrl(url)
            //数据库中并没有任务,这是一个新的下载任务
            if (downloadInfo == null) {
                downloadInfo = DownloadInfo(
                    url = url, action = action, type = type, flag = flag, group = group,
                    path = path, fileName = fileName, childUrl = childUrl, data = data
                )
            }
            //将原本正在下载中的任务恢复到暂停状态,防止意外退出出现的状态错误
            if (downloadInfo!!.status == DownloadInfo.LOADING) {
                downloadInfo!!.status = DownloadInfo.PAUSE
            }
            RoomClient.dataBase.downloadDao().insertOrReplace(downloadInfo!!)
            withContext(Dispatchers.Main) {
                liveData?.value = downloadInfo!!
            }
        }
    }

    /**
     * 取消
     */
    fun start() {
        L.d("wwc start download $download , isActive = ${download?.isActive} , isCancelled = ${download?.isCancelled}")
        if (download != null && (download!!.isActive || !download!!.isCancelled)) {
            return
        }
        download = coroutineScope.launch {
            // 开始
            flow {
                if (downloadInfo == null) {
                    // 下载前准备
                    downloadInfo = RoomClient.dataBase.downloadDao().queryByUrl(url)
                }
                //数据库中并没有任务,这是一个新的下载任务
                if (downloadInfo == null) {
                    downloadInfo = DownloadInfo(
                        url = url, action = action, type = type, flag = flag, group = group,
                        path = path, fileName = fileName, childUrl = childUrl, data = data
                    )
                }
                downloadInfo?.let {
                    it.status = DownloadInfo.LOADING
                    RoomClient.dataBase.downloadDao().insertOrReplace(it)
                    emit(it)
                    var startPosition = it.currentLength
                    //验证断点有效性
                    if (startPosition < 0) throw IllegalArgumentException("Start position less than zero")
                    //下载的文件是否已经被删除
                    if (startPosition > 0 && !TextUtils.isEmpty(it.path)) {
                        val filePath = "${it.path!!}${if (DownloadUtil2.needDownloadSuffix) ".download" else ""}"
                        if (!File(filePath).exists()) throw FileNotFoundException("File does not exist, $filePath file has been deleted!")
                    }
                    val result = DownloadUtil2.downloader.download(
                        start = "bytes=$startPosition-",
                        url = it.url
                    )
                    if (result is Result.Success) {
                        //文件长度 由於帶了 “bytes=startPosition-” 這個header 所以需要判斷
                        if (it.contentLength < 0) {
                            it.contentLength = result.contentLength
                        }
                        //保存的文件名称
                        if (TextUtils.isEmpty(it.fileName)) {
                            it.fileName = UrlUtils.getUrlFileName(it.url)
                        }
                        //创建File,如果已经指定文件path,将会使用指定的path,如果没有指定将会使用默认的下载目录
                        val file: File
                        val tempFile: File
                        if (TextUtils.isEmpty(it.path)) {
                            val fileName =
                                "${it.fileName!!}${if (DownloadUtil2.needDownloadSuffix) ".download" else ""}"
                            file = File(DownloadUtil2.downloadFolder, it.fileName!!)
                            tempFile = File(DownloadUtil2.downloadFolder, fileName)
                            it.path = file.absolutePath
                        } else {
                            val filePath =
                                "${it.path!!}${if (DownloadUtil2.needDownloadSuffix) ".download" else ""}"
                            file = File(it.path!!)
                            tempFile = File(filePath)
                        }
                        //再次验证下载的文件是否已经被删除
                        if (startPosition > 0 && !file.exists() && !tempFile.exists()) throw FileNotFoundException(
                            "File does not exist"
                        )
                        //再次验证断点有效性
                        if (startPosition > it.contentLength){
//                            throw IllegalArgumentException("Start position greater than content length")
                            startPosition = 0
                        }
                        //验证下载完成的任务与实际文件的匹配度
                        if (startPosition == it.contentLength && startPosition > 0) {
                            if ((file.exists() || tempFile.exists()) && startPosition == file.length()) {
                                it.status = DownloadInfo.DONE
                                RoomClient.dataBase.downloadDao().insertOrReplace(it)
                                rename(it)
                                emit(it)
                            } else {
                                throw IOException("The content length is not the same as the file length")
                            }
                        } else {
                            //写入文件
                            val randomAccessFile = RandomAccessFile(tempFile, "rw")
                            randomAccessFile.seek(startPosition)
                            it.currentLength = startPosition
                            val inputStream = result.byteStream
                            val bufferSize = 1024 * 8
                            val buffer = ByteArray(bufferSize)
                            val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
                            var readLength: Int
                            try {
                                while (bufferedInputStream.read(
                                        buffer, 0, bufferSize
                                    ).also { bytes ->
                                        readLength = bytes
                                    } != -1 && it.status == DownloadInfo.LOADING && download?.isActive == true//isActive保证任务能被及时取消
                                ) {
                                    randomAccessFile.write(buffer, 0, readLength)
                                    it.currentLength += readLength
                                    val currentTime = System.currentTimeMillis()
                                    if (currentTime - it.lastRefreshTime > 300) {
                                        it.status = DownloadInfo.LOADING
                                        it.lastRefreshTime = currentTime
                                        RoomClient.dataBase.downloadDao().insertOrReplace(it)
                                        emit(it)
                                    }
                                }
                            } finally {
                                inputStream.close()
                                randomAccessFile.close()
                                bufferedInputStream.close()
                            }

                            L.d("wwc DONE isCancelled ${download?.isCancelled}")
                            if (download?.isCancelled == false) {
                                it.currentLength = it.contentLength
                                it.status = DownloadInfo.DONE
                                RoomClient.dataBase.downloadDao().insertOrReplace(it)
                                rename(it)
                                emit(it)
                            }
                        }
                    } else if (result is Result.Error) {
                        it.status = DownloadInfo.ERROR
                        it.message = result.message
                        RoomClient.dataBase.downloadDao().insertOrReplace(it)
                        emit(it)
                    }
                }
            }
                .flowOn(Dispatchers.IO)
                .cancellable()
                .catch {
                    // 异常
                    if (downloadInfo != null) {
                        downloadInfo!!.status = DownloadInfo.ERROR
                        downloadInfo!!.message = it.message
                        RoomClient.dataBase.downloadDao().insertOrReplace(downloadInfo!!)
                        emit(downloadInfo!!)
                    }
                    L.d("wwc catch")
                }
                .onCompletion {
                    if (downloadInfo?.status == DownloadInfo.DONE) {
                        L.d("wwc onCompletion remove")
                        DownloadUtil2.remove(this@DownloadTask)
                    }
                }
                .collect {
                    coroutineScope.launch(Dispatchers.Main) {
                        // 结果
                        liveData?.value = it
                        L.d("wwc collect $liveData $it")
                    }
                }
        }
    }

    /**
     * 取消
     */
    fun cancel() {
        download?.cancel()
        if (downloadInfo != null) {
            coroutineScope.launch(Dispatchers.IO) {
                downloadInfo!!.reset()
                withContext(Dispatchers.Main) {
                    liveData?.value = downloadInfo!!
                }
                RoomClient.dataBase.downloadDao().delete(downloadInfo!!)
                //同时删除已下载的文件
                downloadInfo!!.path?.let { path ->
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
    }

    /**
     * 暫停
     */
    fun pause() {
        download?.cancel()
        if (downloadInfo != null) {
            coroutineScope.launch(Dispatchers.IO) {
                downloadInfo!!.status = DownloadInfo.PAUSE
                RoomClient.dataBase.downloadDao().insertOrReplace(downloadInfo!!)
                withContext(Dispatchers.Main) {
                    liveData?.value = downloadInfo!!
                }
            }
        }
    }

    private fun rename(downloadInfo: DownloadInfo) {
        if (DownloadUtils.needDownloadSuffix) {
            downloadInfo.path?.let { path ->
                val file = File(path)
                val tempFile = File("$path.download")
                if (!file.exists() && tempFile.exists()) {
                    tempFile.renameTo(file)
                }
            }
        }
    }

}