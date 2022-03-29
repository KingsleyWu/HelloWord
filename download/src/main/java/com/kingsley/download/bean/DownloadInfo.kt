package com.kingsley.download.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

/**
 * 下載的信息
 *
 * @param url 下載用的url
 * @param action 如需要跳轉的動作可以放到此位置
 * @param type 下載的文件類型，用於標識下載文件
 * @param flag flag
 * @param group 可以用於做識別是否是組合下載
 * @param path 保存的地址
 * @param fileName 下載保存的文件名稱
 * @param contentLength 下載文件的長度
 * @param currentLength 已經下載的長度
 * @param status 下載的狀態 [NONE] 0 无状态,[WAITING] 1 等待中,[LOADING] 2 下载中,[PAUSE] 3 暂停,[ERROR] 4 错误,[DONE] 5 完成
 * @param message 下載的消息，如異常錯誤消息
 * @param lastRefreshTime 上一次更新的時間
 * @param childUrl 子下載Url
 * @param data 跟下载相关的数据信息
 */
@Entity
@TypeConverters(Converters::class)
data class DownloadInfo(
    @PrimaryKey
    var url: String = "",
    var action: String? = "",
    var type: String? = "",
    var flag: String? = "",
    var group: String? = null,
    var path: String? = null,
    var fileName: String? = null,
    var contentLength: Long = -1,
    var currentLength: Long = 0,
    var status: Int = NONE,
    var message: String? = "",
    var lastRefreshTime: Long = 0,
    var childUrl: String? = null,
    var data: Serializable? = null
) : Serializable {

    companion object Status {
        private const val serialVersionUID = 111L

        /** 无状态 0 */
        const val NONE = 0

        /** 等待中 1 */
        const val WAITING = 1

        /** 下载中 2 */
        const val LOADING = 2

        /** 暂停 3 */
        const val PAUSE = 3

        /** 错误 4 */
        const val ERROR = 4

        /** 完成 5 */
        const val DONE = 5
    }

    /**
     * 重置任务
     */
    fun reset() {
        contentLength = -1
        currentLength = 0
        status = NONE
        message = ""
        lastRefreshTime = 0
    }
}
