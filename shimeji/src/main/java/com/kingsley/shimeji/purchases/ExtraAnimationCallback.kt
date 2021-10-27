package com.kingsley.shimeji.purchases

import com.kingsley.shimeji.mascot.animations.Animation
import org.solovyev.android.checkout.Purchase

import org.solovyev.android.checkout.Inventory
import org.solovyev.android.checkout.Inventory.Products


class ExtraAnimationCallback : Inventory.Callback {
    override fun onLoaded(paramProducts: Products) {
        val list: List<Purchase> = paramProducts["inapp"].purchases
        Animation.paidEnabled = false
        Animation.flingEnabled = false
        for (purchase in list) {
            if (purchase.state == Purchase.State.PURCHASED || purchase.state == Purchase.State.REFUNDED) {
                val str = purchase.sku
                str.hashCode()
                if (str != "physics_upgrade") {
                    if (str != "extra_animations") continue
                    Animation.paidEnabled = true
                    continue
                }
                Animation.flingEnabled = true
            }
        }
    }
}
