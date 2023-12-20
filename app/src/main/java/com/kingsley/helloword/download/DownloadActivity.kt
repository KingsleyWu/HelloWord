package com.kingsley.helloword.download

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.kingsley.base.activity.BaseVmVbActivity
import com.kingsley.common.L
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.bean.DownloadGroup
import com.kingsley.download.utils.DownloadUtils
import com.kingsley.helloword.databinding.DownloadActivityBinding
import kotlinx.coroutines.launch

class DownloadActivity : BaseVmVbActivity<DownloadViewModel, DownloadActivityBinding>() {
    private var appHomeAdapter: DownloadAppAdapter? = null

    /**
     * App列表数据Observer
     */
    private val appListDataObserver: Observer<MutableList<App>> = Observer {
        appHomeAdapter?.setData(it)
    }
    private var data: DownloadGroup? = DownloadGroup(
        id = "test",
        type = "game",
        notificationTitle = "test download",
        request = DownloadInfo(
            url = "https://imtt.dd.qq.com/16891/apk/C10B2237586138DF3909E47B981B73F9.apk",
            notifyTitle = "QQ",
            notifyContent = "QQ",
            fileType = "game",
            flag = "QQ",
            child = DownloadInfo(
                url = "https://imtt.dd.qq.com/16891/apk/53087BD86B84CB6F728E9B57256B3A30.apk",
                notifyTitle = "欢乐斗地主",
                notifyContent = "欢乐斗地主",
                fileType = "apk",
                flag = "欢乐斗地主",
                child = DownloadInfo(
                    url = "https://imtt.dd.qq.com/16891/apk/D36983B2255AA65BE5121B66B7FAE8A9.apk",
                    notifyTitle = "天天爱消除(葫芦兄弟版)",
                    notifyContent = "天天爱消除(葫芦兄弟版)",
                    fileType = "apk",
                    flag = "天天爱消除(葫芦兄弟版)",
                ),
            ),
        ),
    )

    override fun initView(savedInstanceState: Bundle?) {
        mViewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        val itemAnimator = mViewBinding.recyclerView.itemAnimator
        if (itemAnimator is SimpleItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }
        appHomeAdapter = DownloadAppAdapter(lifecycleOwner = this)
        mViewBinding.recyclerView.adapter = appHomeAdapter
        val download = DownloadUtils.request(data!!)
        mViewBinding.tvTestDownload.setOnClickListener {

            lifecycleScope.launch {
                if (data?.status != DownloadInfo.DONE) {
//                    if (data?.status == DownloadRequest.DOWNLOADING) {
//                        download.
//                    } else {
                        download.download(this@DownloadActivity) {
                            L.d("data Percent : ${it.getProgressPercent()}, detail : $it")
//                        }
                    }
                } else {
                    L.d("data DONE")
                }
            }
        }


    }

    override fun initObserve() {
        mViewModel.appListData.observe(this, appListDataObserver)
        mViewModel.requestAppListData()
    }

}