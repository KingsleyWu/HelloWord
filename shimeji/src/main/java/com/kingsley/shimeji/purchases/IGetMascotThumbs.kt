package com.kingsley.shimeji.purchases

import android.content.Context
import com.kingsley.shimeji.mascotlibrary.MascotListing

import android.graphics.Bitmap

interface IGetMascotThumbs {
    fun addMascot(context: Context, mascotListing: MascotListing, bitmapList: List<Bitmap>)
    val allMascotIds: MutableList<Int>
    val allThumbs: MutableList<MascotListing>

    fun getThumbById(paramInt: Int): MascotListing?
}