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
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.core.DownloadUtil
import com.kingsley.helloword.R
import com.kingsley.helloword.databinding.ItemDownloadAppLayoutBinding

class DownloadAppAdapter(
    private val lifecycleOwner: LifecycleOwner
) : BaseAdapter<App, DownloadViewHolder>() {

    inner class DownloadObserver(private val tag: Any, private val holder: DownloadViewHolder) :
        Observer<DownloadInfo> {

        override fun onChanged(t: DownloadInfo?) {
            if (tag == holder.tag) {
                try {
                    notifyItemChanged(holder.adapterPosition)
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        with(holder.itemViewBind) {
            val item = getItem(position)
            holder.tag = item.downloadUrl!!
            Glide.with(ivIcon).load(item.icon).into(ivIcon)
            tvName.text = item.name
            val downloadScope = DownloadUtil.request(url = item.downloadUrl, data = item)
            downloadScope?.observer(lifecycleOwner, DownloadObserver(holder.tag, holder))
            tbStatus.text = "下载"
            tbStatus.setTextColor(Color.argb(0xFF, 0x11, 0xB0, 0x77))
            val downloadInfo = downloadScope?.downloadInfo()
            progressBar.visibility = if (downloadInfo == null) View.GONE else View.VISIBLE
            tbStatus.setOnClickListener {
                val scope = DownloadUtil.request(url = item.downloadUrl, data = item)
                val info = scope?.downloadInfo()
                if (info != null) {
                    when (info.status) {
                        DownloadInfo.ERROR, DownloadInfo.PAUSE, DownloadInfo.NONE -> scope.start()
                        DownloadInfo.LOADING -> scope.pause()
                    }
                }
            }
            tbCancel.setOnClickListener {
                val scope = DownloadUtil.request(url = item.downloadUrl, data = item)
                val info = scope?.downloadInfo()
                if (info != null) {
                    scope.remove()
                }
            }
            downloadInfo?.let {
                val progressPercent =
                    (it.currentLength.toFloat() / it.contentLength * 100).toInt()
                progressBar.progress = progressPercent
                when (it.status) {
                    DownloadInfo.LOADING -> {
                        tbStatus.text = String.format("%s%s", progressPercent, "%")
                        progressBar.visibility = View.VISIBLE
                        tbStatus.setTextColor(Color.argb(0xFF, 0x11, 0xB0, 0x77))
                        tbStatus.setBackgroundResource(R.drawable.bt_background_normal)
                    }
                    DownloadInfo.PAUSE -> {
                        tbStatus.text = "继续"
                        progressBar.visibility = View.VISIBLE
                        tbStatus.setTextColor(Color.argb(0xFF, 0x11, 0xB0, 0x77))
                        tbStatus.setBackgroundResource(R.drawable.bt_background_normal)
                    }
                    DownloadInfo.WAITING -> {
                        tbStatus.text = "等待"
                        progressBar.visibility = View.VISIBLE
                        tbStatus.setTextColor(Color.argb(0xFF, 0x11, 0xB0, 0x77))
                        tbStatus.setBackgroundResource(R.drawable.bt_background_normal)
                    }
                    DownloadInfo.ERROR -> {
                        tbStatus.text = "重试"
                        progressBar.visibility = View.GONE
                        tbStatus.setTextColor(Color.RED)
                        tbStatus.setBackgroundResource(R.drawable.bt_background_error)
                    }
                    DownloadInfo.DONE -> {
                        tbStatus.text = "完成"
                        tbStatus.setTextColor(Color.DKGRAY)
                        progressBar.visibility = View.GONE
                        tbStatus.setBackgroundResource(R.drawable.bt_background_done)
                    }
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
        DownloadViewHolder(ItemDownloadAppLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}

class DownloadViewHolder(val itemViewBind: ItemDownloadAppLayoutBinding) : BaseViewHolder<App>(itemViewBind.root) {
    var tag: String = ""
}