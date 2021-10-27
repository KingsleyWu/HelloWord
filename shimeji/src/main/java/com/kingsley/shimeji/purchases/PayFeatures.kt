package com.kingsley.shimeji.purchases

object PayFeatures {
    const val EXTRA_ANIMATIONS = "extra_animations"
    const val EXTRA_SLOTS = "extra_slots"
    const val FULL_PRICE = "full_price"
    const val PHYSICS_UPGRADE = "physics_upgrade"
    val list: List<String>
        get() = mutableListOf<String>().apply {
            add("extra_slots")
            add("full_price")
            add("extra_animations")
            add("physics_upgrade")
        }
}
