package com.kingsley.helloword.keyboard

import android.net.Uri
import com.kingsley.helloword.keyboard.AttachmentsRecyclerViewAdapter.MyViewHolder
import androidx.appcompat.widget.AppCompatImageView
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kingsley.helloword.R
import java.util.ArrayList

class AttachmentsRecyclerViewAdapter(attachments: List<Uri>) : RecyclerView.Adapter<MyViewHolder>() {
    class MyViewHolder(var mAttachmentThumbnailView: AppCompatImageView) :
        RecyclerView.ViewHolder(
            mAttachmentThumbnailView
        )

    private val mAttachments: MutableList<Uri> = ArrayList(attachments)

    fun addAttachments(uris: Collection<Uri>) {
        mAttachments.addAll(uris)
    }

    fun clearAttachments() {
        mAttachments.clear()
    }

    override fun getItemCount(): Int {
        return mAttachments.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attachment, parent, false) as AppCompatImageView
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val uri = mAttachments[position]
        Glide.with(holder.mAttachmentThumbnailView).load(uri).into(holder.mAttachmentThumbnailView)
        holder.mAttachmentThumbnailView.clipToOutline = true
    }
}