package com.kingsley.net.download

import androidx.lifecycle.MutableLiveData
import com.kingsley.download.bean.DownloadInfo
import kotlinx.coroutines.CoroutineScope

/**
 * 下載組合
 */
class DownloadGroupTask(
    /** 协程作用域 */
    var coroutineScope: CoroutineScope,
    /** 监听器 */
    var liveData: MutableLiveData<DownloadInfo>? = null,
    /** 下載列表 */
    var downloadList: List<DownloadTask>
) {

    fun start() {

    }
}