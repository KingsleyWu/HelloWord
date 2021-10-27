package com.kingsley.shimeji.data

import android.content.Context
import com.kingsley.shimeji.mascotlibrary.MascotListing

import android.graphics.Bitmap
import com.kingsley.shimeji.data.Helper.getActiveTeamMembers
import com.kingsley.shimeji.mascotselector.ActiveMascots

import com.kingsley.shimeji.purchases.IGetMascotThumbs


class TeamListingService private constructor(context: Context) : IGetMascotThumbs {
    var activeMascots: ActiveMascots
    override var allThumbs: MutableList<MascotListing> = ArrayList()
        private set

    override fun addMascot(context: Context, mascotListing: MascotListing, bitmapList: List<Bitmap>) {
        mascotListing.thumbnail = bitmapList[0]
        allThumbs.add(mascotListing)
        val mascotDBHelper = MascotDBHelper(context)
        mascotDBHelper.addMascotToDatabase(mascotListing.id, mascotListing.name, bitmapList)
        mascotDBHelper.close()
    }

    override val allMascotIds: MutableList<Int>
        get() {
            val arrayList: ArrayList<Int> = ArrayList()
            val iterator = allThumbs.iterator()
            while (iterator.hasNext()) arrayList.add(Integer.valueOf(iterator.next().id))
            return arrayList
        }

    override fun getThumbById(paramInt: Int): MascotListing? {
        for (mascotListing in allThumbs) {
            if (mascotListing.id == paramInt) return mascotListing
        }
        return null
    }

    fun hasTeamMember(paramInt: Int): Boolean {
        val iterator = allThumbs.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().id == paramInt) return true
        }
        return false
    }

    companion object {
        private var instance: TeamListingService? = null
        fun getInstance(paramContext: Context): TeamListingService? {
            if (instance == null) instance = TeamListingService(paramContext)
            return instance
        }
    }

    init {
        val mascotDBHelper = MascotDBHelper(context)
        allThumbs = mascotDBHelper.mascotThumbnails
        mascotDBHelper.close()
        activeMascots = ActiveMascots(getActiveTeamMembers(context), this)
    }
}
