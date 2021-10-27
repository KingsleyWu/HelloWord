package com.kingsley.shimeji.mascotselector

import android.graphics.Bitmap


interface SelectorView {
    fun emptySlot(position: Int)
    fun fillSlot(position: Int, bitmap: Bitmap?)
    fun fillSlotWithErrorImage(position: Int)
    val isDisplayServiceRunning: Boolean

    fun lockSlot(position: Int)
    fun notifyLastSlotEmpty()
    fun saveMascotSelection(paramList: List<Int>)
    fun setSwitchChangeListener(action: OnDisplayMascotsAction?)
    fun setSwitchChecked(paramBoolean: Boolean)
    fun startDisplayService(): Boolean
    fun stopDisplayService()
    interface OnDisplayMascotsAction {
        fun hideMascots()
        fun showMascots()
    }
}
