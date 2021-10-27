package com.kingsley.shimeji.mascotselector

import com.kingsley.shimeji.mascotlibrary.MascotListing


internal interface SelectorPresenter {
    fun addMascot(paramMascotListing: MascotListing)
    fun clearMascot(position: Int)
    fun loadMascots(paramActiveMascots: ActiveMascots)
    fun setDisplayServiceUI()
    fun updateMascots()
}
