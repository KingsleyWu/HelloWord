package com.kingsley.helloword.download

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.kingsley.base.adapter.BaseAdapter
import com.kingsley.base.adapter.BaseViewHolder
import com.kingsley.common.L
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.bean.DownloadGroup
import com.kingsley.download.core.DownloadTask
import com.kingsley.download.utils.DownloadUtils
import com.kingsley.helloword.R
import com.kingsley.helloword.databinding.ItemDownloadAppLayoutBinding

class DownloadAppAdapter(
    private val lifecycleOwner: LifecycleOwner
) : BaseAdapter<App, DownloadViewHolder>() {

    inner class DownloadObserver(private val tag: Any, private val holder: DownloadViewHolder) :
        Observer<DownloadGroup> {

        override fun onChanged(value: DownloadGroup) {
            if (tag == holder.tag) {
                try {
                    notifyItemChanged(holder.layoutPosition)
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int, payloads: List<Any>) {
        with(holder.itemViewBind) {
            val item = getItem(position)
            holder.tag = item.downloadUrl
            Glide.with(ivIcon).load(item.icon).into(ivIcon)
            tvName.text = item.name
            holder.task = DownloadUtils.request(item.downloadUrl) {
                it.add(DownloadInfo(url = item.downloadUrl))
            }
            if (holder.task?.liveData?.hasObservers() != true) {
                holder.task?.liveData?.observe(lifecycleOwner, DownloadObserver(holder.tag, holder))
            }

            tbStatus.text = "下载"
            tbStatus.setTextColor(Color.argb(0xFF, 0x11, 0xB0, 0x77))
            val data = holder.task?.data
            progressBar.visibility = if (data == null) View.GONE else View.VISIBLE
            tbStatus.setOnClickListener {
                holder.task?.let {
                    when {
                        it.isDownloading() -> it.pause()
                        it.canDownload() -> it.download()
                    }
                }
            }
            tbCancel.setOnClickListener {
                holder.task?.cancel()
            }
            data?.let {
                val progress = it.getProgress()
                val progressPercent = (progress.second.toFloat() / progress.first * 100).toInt()
                progressBar.progress = progressPercent
                L.d("status， 當前 status = ${data.status}， percent = ${data.percent()}")
                when (it.status) {
                    // 1
                    DownloadInfo.WAITING -> {
                        tbStatus.text = "等待"
                        progressBar.visibility = View.VISIBLE
                        tbStatus.setTextColor(Color.argb(0xFF, 0x11, 0xB0, 0x77))
                        tbStatus.setBackgroundResource(R.drawable.bt_background_normal)
                    }
                    // 2
                    DownloadInfo.DOWNLOADING -> {
                        tbStatus.text = String.format("%s%s", progressPercent, "%")
                        progressBar.visibility = View.VISIBLE
                        tbStatus.setTextColor(Color.argb(0xFF, 0x11, 0xB0, 0x77))
                        tbStatus.setBackgroundResource(R.drawable.bt_background_normal)
                    }
                    // 3, 4
                    DownloadInfo.PAUSE, DownloadInfo.PAUSED -> {
                        tbStatus.text = "继续"
                        progressBar.visibility = View.VISIBLE
                        tbStatus.setTextColor(Color.argb(0xFF, 0x11, 0xB0, 0x77))
                        tbStatus.setBackgroundResource(R.drawable.bt_background_normal)
                    }
                    // 5
                    DownloadInfo.FAILED -> {
                        tbStatus.text = "重试"
                        progressBar.visibility = View.GONE
                        tbStatus.setTextColor(Color.RED)
                        tbStatus.setBackgroundResource(R.drawable.bt_background_error)
                    }
                    // 6
                    DownloadInfo.DONE -> {
                        tbStatus.text = "完成"
                        tbStatus.setTextColor(Color.DKGRAY)
                        progressBar.visibility = View.GONE
                        tbStatus.setBackgroundResource(R.drawable.bt_background_done)
                    }
                    // 7
                    DownloadInfo.PENDING -> {
                        tbStatus.text = "已加入隊列，等待下載中"
                        tbStatus.setTextColor(Color.DKGRAY)
                        progressBar.visibility = View.GONE
                        tbStatus.setBackgroundResource(R.drawable.bt_background_done)
                    }
                    // 0
                    else -> {
                        tbStatus.text = "下载"
                        progressBar.visibility = View.GONE
                        tbStatus.setTextColor(Color.argb(0xFF, 0x11, 0xB0, 0x77))
                        tbStatus.setBackgroundResource(R.drawable.bt_background_normal)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DownloadViewHolder(
            ItemDownloadAppLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onViewDetachedFromWindow(holder: DownloadViewHolder) {
        super.onViewDetachedFromWindow(holder)
    }
}

class DownloadViewHolder(val itemViewBind: ItemDownloadAppLayoutBinding) :
    BaseViewHolder<App>(itemViewBind.root) {
    var tag: String = ""
    var task: DownloadTask? = null
}