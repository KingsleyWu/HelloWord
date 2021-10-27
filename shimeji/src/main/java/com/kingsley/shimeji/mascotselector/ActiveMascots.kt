package com.kingsley.shimeji.mascotselector

import com.kingsley.shimeji.mascotlibrary.MascotListing

import com.kingsley.shimeji.purchases.IGetMascotThumbs


class ActiveMascots(paramList: List<Int>, paramIGetMascotThumbs: IGetMascotThumbs) :
    ArrayList<MascotListing?>() {
    private var mascotLimit = 2
    private var nextInsertPosition: Int

    override fun add(element: MascotListing?): Boolean {
        val i: Int = size
        val j = mascotLimit
        if (i < j) {
            nextInsertPosition++
            return super.add(element)
        }
        if (nextInsertPosition >= j) nextInsertPosition = 0
        set(nextInsertPosition, element)
        nextInsertPosition++
        return true
    }

    override fun get(index: Int): MascotListing? {
        return if (index >= size) null else super.get(index)
    }

    val mascotIDs: List<Int>
        get() {
            val arrayList: ArrayList<Int> = ArrayList()
            var b: Byte = 0
            while (b < size && b < mascotLimit) {
                if (get(b.toInt()) != null) arrayList.add(Integer.valueOf(get(b.toInt())!!.id))
                b++
            }
            return arrayList
        }

    fun isOutOfBounds(paramInt: Int): Boolean {
        return paramInt >= mascotLimit
    }

    fun mascotExistsAt(paramInt: Int): Boolean {
        return get(paramInt) != null
    }

    override fun removeAt(index: Int): MascotListing? {
        if (index >= size) return null
        nextInsertPosition = index
        return super.removeAt(index)
    }

    fun setMascotLimit(paramInt: Int) {
        mascotLimit = paramInt
        nextInsertPosition = size
    }

    init {
        val iterator = paramList.iterator()
        while (iterator.hasNext()) {
            super.add(paramIGetMascotThumbs.getThumbById(iterator.next()))
        }
        nextInsertPosition = mascotLimit
    }
}
