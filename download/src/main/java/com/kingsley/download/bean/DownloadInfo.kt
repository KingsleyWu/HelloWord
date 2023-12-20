package com.kingsley.download.bean

import androidx.room.*
import com.kingsley.download.bean.DownloadInfo.Status.DONE
import com.kingsley.download.bean.DownloadInfo.Status.DOWNLOADING
import com.kingsley.download.bean.DownloadInfo.Status.FAILED
import com.kingsley.download.bean.DownloadInfo.Status.NONE
import com.kingsley.download.bean.DownloadInfo.Status.PAUSE
import com.kingsley.download.bean.DownloadInfo.Status.PAUSED
import com.kingsley.download.bean.DownloadInfo.Status.WAITING
import com.kingsley.download.utils.*
import java.io.File
import java.io.Serializable
import java.math.RoundingMode

/**
 * 下載的信息
 *
 * @param url 下載用的url
 * @param contentUri content://com.xxx.xxx.DownloadProvider/xxx/com.xxx.xxx/1.0.0
 * @param notifyTitle 通知標題
 * @param notifyContent 通知內容
 * @param notifyClickUri 如需要跳轉的動作可以放到此位置
 * @param fileType 下載的文件類型，用於標識下載文件
 * @param flag flag
 * @param path 保存的地址
 * @param fileName 下載保存的文件名稱
 * @param contentLength 下載文件的長度
 * @param currentLength 已經下載的長度
 * @param status 下載的狀態 [NONE] 0 无状态,[WAITING] 1 等待中,[DOWNLOADING] 2 下载中,[PAUSE] 3 暂停中,[PAUSED] 4 已暂停,[FAILED] 5 错误,[DONE] 6 完成
 * @param message 下載的消息，如異常錯誤消息
 * @param createTime 創建時間
 * @param updateTime 上一次更新的時間
 * @param data 跟下载相关的数据信息
 * @param redirectUrl 下載用的url, 重定向後的 url
 * @param child 下一個下載信息
 */
data class DownloadInfo @Ignore constructor(
    var url: String = "",
    var contentUri: String? = null,
    var notifyTitle: String? = "",
    var notifyContent: String? = "",
    var notifyClickUri: String? = null,
    var fileType: String? = "",
    var flag: String? = "",
    var path: String = "",
    var fileName: String? = null,
    var contentLength: Long = -1,
    var currentLength: Long = 0,
    var status: Int = NONE,
    var message: String? = "",
    var createTime: Long = System.currentTimeMillis(),
    var updateTime: Long = 0,
    var data: String? = null,
    var redirectUrl: String = "",
    var child: DownloadInfo? = null
) : Serializable {

    constructor() : this("")

    companion object Status {
        private const val serialVersionUID = 1111L

        /** 无状态 0 */
        const val NONE = 0

        /** 開始下載 等待中，連接中 1 */
        const val WAITING = 1

        /** 下载中 2 */
        const val DOWNLOADING = 2

        /** 暂停中 3 */
        const val PAUSE = 3

        /** 已暂停 4 */
        const val PAUSED = 4

        /** 错误 5 */
        const val FAILED = 5

        /** 完成 6 */
        const val DONE = 6

        /** 在隊列等待下载中 7 */
        const val PENDING = 7
    }

    /**
     * 重置任务
     */
    fun reset() {
        contentLength = -1
        currentLength = 0
        createTime = System.currentTimeMillis()
        updateTime = 0
        status = NONE
        message = ""
        child?.reset()
    }

    /**
     * 更新
     */
    fun update(status: Int, message: String? = "", updateTime: Long = System.currentTimeMillis()) {
        this.status = status
        this.message = message
        this.updateTime = updateTime
    }

    /**
     * 刪除文件
     */
    fun deleteFile() {
        if (path.isNotEmpty()) {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
            val tempFile = File("$path.download")
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
        child?.deleteFile()
    }
}

/**
 * 下載信息的集合 group
 *
 * @param id 唯一，可以使用 urls.hashcode + "_" + type
 * @param objId second id 用於標識
 * @param type 類型
 * @param dirName 保存文件的父文件夹名称
 * @param status 下載的狀態 [NONE] 0 无状态,[WAITING] 1 等待中,[DOWNLOADING] 2 下载中,[PAUSE] 3 暂停中,[PAUSED] 4 已暂停,[FAILED] 5 错误,[DONE] 6 完成
 * @param config 是否需要 wifi, 是否顯示通知
 * @param notificationTitle 通知 完成後的標題
 * @param request 下載的內容
 * @param createTime 創建時間
 * @param updateTime 上一次更新的時間
 * @param totalLength 總長度
 * @param abnormalExit 是否是异常结束
 *
 */
@Entity(
    tableName = DownloadDBUtil.DOWNLOAD_TABLE_NAME,
    indices = [Index("id", unique = true)]
)
@TypeConverters(Converters::class)
data class DownloadGroup(
    @PrimaryKey
    var id: String = "",
    var objId: String = "",
    var type: String = "",
    var dirName: String? = "",
    var status: Int = NONE,
    var message: String? = "",
    var config: Int = 0,
    var notificationTitle: String = "",
    var request: DownloadInfo? = null,
    var createTime: Long = System.currentTimeMillis(),
    var updateTime: Long = 0,
    var totalLength: Long = -1,
    var data: String? = null,
    var abnormalExit: Boolean = false
) {

    /**
     * 重置任务
     */
    fun reset() {
        status = NONE
        message = ""
        createTime = System.currentTimeMillis()
        updateTime = 0
        request?.reset()
    }

    /**
     * 更新
     * @param status status
     * @param message message
     * @param updateTime updateTime
     */
    fun update(status: Int, message: String? = "", updateTime: Long = System.currentTimeMillis()): DownloadGroup {
        this.status = status
        this.message = message
        this.updateTime = updateTime
        return this
    }

    /**
     * 更新
     * @param totalLength totalLength
     * @param updateTime updateTime
     */
    fun update(totalLength: Long, updateTime: Long = System.currentTimeMillis()): DownloadGroup {
        this.totalLength = totalLength
        this.updateTime = updateTime
        return this
    }

    /**
     * 獲取進度 first -> totalSize， second -> currentDownloadSize
     */
    fun getProgress() : Pair<Long, Long> {
        var total = 0L
        var current = 0L
        var temp = request
        while(temp != null) {
            total += temp.contentLength
            current += temp.currentLength
            temp = temp.child
        }
        return Pair(total, current)
    }

    /**
     * Return percent number.
     */
    fun percent(): Double {
        val progress = getProgress()
        return progress.second ratio progress.first
    }

    /**
     * Return percent string. xx%
     */
    fun getProgressPercent(): String {
        return "${percent()}%"
    }

    private infix fun Long.ratio(bottom: Long): Double {
        if (bottom <= 0) {
            return 0.0
        }
        val result = (this * 100.0).toBigDecimal().divide((bottom * 1.0).toBigDecimal(), 2, RoundingMode.HALF_UP)
        return result.toDouble()
    }

    internal fun validate(): Boolean {
        val urlList = mutableListOf<String>()
        var temp = request
        var size = 0
        while(temp != null) {
            urlList.add(temp.redirectUrl.ifEmpty { temp!!.url })
            temp = temp.child
            size++
        }
        val urlSet = urlList.toSet()
        if (urlSet.size != size) {
            update(FAILED, "Duplicate url !!!")
            return true
        }
        return false
    }

    internal fun urlEmptyOrNull(): Boolean {
        if (request == null || request?.url.isNullOrEmpty()) {
            return true
        }
        var temp = request
        while(temp != null) {
            temp = temp.child
            if (temp != null && temp.url.isEmpty()) {
                return true
            }
        }
        return false
    }

    fun buildId(): String {
        return "${hashCode()}"
    }

    /**
     * 並行下載 [CONFIG_PARALLEL] = 2
     * 下載需要 WIFI [CONFIG_WIFI_NEED] = 4
     * 下載顯示通知 [CONFIG_SHOW_NOTIFICATION] = 8
     * 下載時重新覆蓋文件，即 刪除原來下載的文件 [CONFIG_DOWNLOAD_REPLACE] = 16
     * 下载失败后，保持已下载的文件数据 [CONFIG_OCCUR_ERROR_KEEP_FILE] = 32
     */
    fun checkConfig(optionConfig: Int): Boolean {
        return optionConfig == optionConfig and this.config
    }
    /**
     * 並行下載 [CONFIG_PARALLEL] = 2
     */
    fun isParallel(): Boolean {
        return checkConfig(CONFIG_PARALLEL)
    }

    /**
     * 下載需要 WIFI [CONFIG_WIFI_NEED] = 4
     */
    fun needWifi() = checkConfig(CONFIG_WIFI_NEED)

    /**
     * 下載顯示通知 [CONFIG_SHOW_NOTIFICATION] = 8
     */
    fun showNotification() = checkConfig(CONFIG_SHOW_NOTIFICATION)

    /**
     * 下載時重新覆蓋文件，即 刪除原來下載的文件 [CONFIG_DOWNLOAD_REPLACE] = 16
     */
    fun downloadReplace() = checkConfig(CONFIG_DOWNLOAD_REPLACE)

    /**
     * 下载失败后，保持已下载的文件数据 [CONFIG_OCCUR_ERROR_KEEP_FILE] = 32
     */
    fun occurErrorKeepFile() = checkConfig(CONFIG_OCCUR_ERROR_KEEP_FILE)
}

/**
 * DownloadGroup 的 Builder 類
 */
class DGBuilder {

    private var data = DownloadGroup()

    /** id  */
    fun id(id: String): DGBuilder {
        data.id = id
        return this
    }

    /** objId  */
    fun objId(objId: String): DGBuilder {
        data.objId = objId
        return this
    }

    /** 類型  */
    fun type(type: String = DEFAULT_TYPE): DGBuilder {
        data.type = type
        return this
    }

    /** 保存文件的父文件夹名称  */
    fun dirName(dirName: String? = ""): DGBuilder {
        data.dirName = dirName
        return this
    }

    /**
     * 並行下載 [CONFIG_PARALLEL] = 2
     * 下載需要 WIFI [CONFIG_WIFI_NEED] = 4
     * 下載顯示通知 [CONFIG_SHOW_NOTIFICATION] = 8
     * 下載時重新覆蓋文件，即 刪除原來下載的文件 [CONFIG_DOWNLOAD_REPLACE] = 16
     * 下载失败后，保持已下载的文件数据 [CONFIG_OCCUR_ERROR_KEEP_FILE] = 32
     * eg:
     * var config = 0
     * config = config or if (paralleled) CONFIG_PARALLEL else 0
     * config = config or if (needWifi) CONFIG_WIFI_NEED else 0
     * config = config or if (showNotification) CONFIG_SHOW_NOTIFICATION else 0
     * config = config or if (downloadReplace) CONFIG_DOWNLOAD_REPLACE else 0
     * config = config or if (occurErrorKeepFile) CONFIG_OCCUR_ERROR_KEEP_FILE else 0
     */
    fun config(config: Int): DGBuilder {
        data.config = config
        return this
    }

    /**
     * 並行下載 [CONFIG_PARALLEL] = 2
     */
    fun paralleled(paralleled: Boolean): DGBuilder {
        data.config = data.config or if (paralleled) CONFIG_PARALLEL else 0
        return this
    }

    /**
     * 下載需要 WIFI [CONFIG_WIFI_NEED] = 4
     */
    fun needWifi(needWifi: Boolean): DGBuilder {
        data.config = data.config or if (needWifi) CONFIG_WIFI_NEED else 0
        return this
    }

    /**
     * 下載顯示通知 [CONFIG_SHOW_NOTIFICATION] = 8
     */
    fun showNotification(showNotification: Boolean): DGBuilder {
        data.config = data.config or if (showNotification) CONFIG_SHOW_NOTIFICATION else 0
        return this
    }

    /**
     * 下載時重新覆蓋文件，即 刪除原來下載的文件 [CONFIG_DOWNLOAD_REPLACE] = 16
     */
    fun downloadReplace(downloadReplace: Boolean): DGBuilder {
        data.config = data.config or if (downloadReplace) CONFIG_DOWNLOAD_REPLACE else 0
        return this
    }

    /**
     * 下载失败后，保持已下载的文件数据 [CONFIG_OCCUR_ERROR_KEEP_FILE] = 32
     */
    fun occurErrorKeepFile(occurErrorKeepFile: Boolean): DGBuilder {
        data.config = data.config or if (occurErrorKeepFile) CONFIG_OCCUR_ERROR_KEEP_FILE else 0
        return this
    }

    /** 添加子任務 */
    fun addChild(child: DownloadInfo): DGBuilder {
        if (data.request == null) {
            data.request = child
        } else {
            addSubChild(data.request!!, child)
        }
        return this
    }

    /** 子任務 */
    private fun addSubChild(parent: DownloadInfo, child: DownloadInfo) {
        val temp = parent.child
        if (temp != null) {
            addSubChild(temp, child)
        } else {
            parent.child = child
        }
    }

    fun build(): DownloadGroup {
        if (data.urlEmptyOrNull()) {
            throw IllegalArgumentException("download request is empty")
        }
        if (data.id.isEmpty()) {
            data.id = data.buildId()
        }
        return data
    }

}