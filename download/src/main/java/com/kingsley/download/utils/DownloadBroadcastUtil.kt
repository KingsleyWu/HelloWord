package com.kingsley.download.utils

import android.content.Intent
import com.kingsley.download.appContext
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.bean.DownloadGroup
import java.io.Serializable


internal object DownloadBroadcastUtil {

    /**
     * 發送廣播
     */
    fun sendBroadcast(action: DownloadAction) {
        val intent = Intent(action.action)
        action.data?.let {
            intent.putExtra(KEY_TASK_ID, it.id)
            if (it.status == DownloadInfo.FAILED) {
                intent.putExtra(KEY_ERROR, it.message)
            }
        }
        appContext.sendBroadcast(intent)
    }

    /**
     * 發送廣播,僅限 DONE, WAITING, FAILED
     */
    fun sendBroadcast(data: DownloadGroup) {
        when (data.status) {
            DownloadInfo.DONE -> sendBroadcast(DownloadAction.Success(data))
            DownloadInfo.WAITING -> sendBroadcast(DownloadAction.Waiting(data))
            DownloadInfo.FAILED -> sendBroadcast(DownloadAction.Failed(data))
            else -> {}
        }
    }

}

sealed class DownloadAction(val action: String, open val data: DownloadGroup? = null) : Serializable {

    companion object {
        const val DOWNLOAD_START = "download.start"
        const val DOWNLOAD_WAITING = "download.waiting"
        const val DOWNLOAD_CANCEL = "download.cancel"
        const val DOWNLOAD_PAUSE = "download.pause"
        const val DOWNLOAD_SUCCESS = "download.success"
        const val DOWNLOAD_FAILED = "download.failed"
        const val DOWNLOAD_JUMP_TO = "download.jump_to"
        const val DOWNLOAD_PENDING_INTENT = "download.pending_intent"
    }

    data class Waiting(override val data: DownloadGroup? = null) : DownloadAction(
        DOWNLOAD_WAITING
    )

    data class Cancel(override val data: DownloadGroup? = null) : DownloadAction(
        DOWNLOAD_CANCEL
    )

    data class Pause(override val data: DownloadGroup? = null) : DownloadAction(
        DOWNLOAD_PAUSE
    )

    data class Success(override val data: DownloadGroup? = null) : DownloadAction(
        DOWNLOAD_SUCCESS
    )

    data class Failed(override val data: DownloadGroup? = null) : DownloadAction(
        DOWNLOAD_FAILED
    )
}