package com.kingsley.shimeji.mascotselector

import com.kingsley.shimeji.mascotlibrary.MascotListing
import java.lang.Exception


internal class SelectorPresenterImpl(paramSelectorView: SelectorView) : SelectorPresenter {
    private lateinit var activeMascots: ActiveMascots
    private val mascotSlots: Int = 6
    private val view: SelectorView = paramSelectorView
    private fun setupSlot(index: Int) {
        when {
            activeMascots.isOutOfBounds(index) -> {
                view.lockSlot(index)
            }
            activeMascots.mascotExistsAt(index) -> {
                try {
                    view.fillSlot(index, activeMascots[index]!!.thumbnail)
                } catch (exception: Exception) {
                    view.fillSlotWithErrorImage(index)
                }
            }
            else -> {
                view.emptySlot(index)
            }
        }
    }

    override fun addMascot(paramMascotListing: MascotListing) {
        activeMascots.add(paramMascotListing)
        view.saveMascotSelection(activeMascots.mascotIDs)
    }

    override fun clearMascot(position: Int) {
        if (position == 0 && activeMascots.size == 1) view.notifyLastSlotEmpty()
        if (activeMascots.removeAt(position) != null) {
            view.saveMascotSelection(activeMascots.mascotIDs)
            updateMascots()
        }
    }

    override fun loadMascots(paramActiveMascots: ActiveMascots) {
        activeMascots = paramActiveMascots
    }

    override fun setDisplayServiceUI() {
        val bool: Boolean = view.isDisplayServiceRunning
        view.setSwitchChecked(bool)
        view.setSwitchChangeListener(object : SelectorView.OnDisplayMascotsAction {
            override fun hideMascots() {
                view.stopDisplayService()
            }

            override fun showMascots() {
                if (!view.startDisplayService()) view.setSwitchChecked(false)
            }
        })
    }

    override fun updateMascots() {
        for (b in 0 until mascotSlots) setupSlot(b)
    }

}
