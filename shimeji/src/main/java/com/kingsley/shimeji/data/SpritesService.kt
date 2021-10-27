package com.kingsley.shimeji.data

import android.content.Context
import com.kingsley.shimeji.mascot.Sprites

import com.kingsley.shimeji.mascot.SpriteUtil

import android.graphics.Bitmap
import android.util.Log
import com.kingsley.shimeji.data.Helper.resizeSprites
import java.lang.StringBuilder
import java.util.concurrent.ConcurrentHashMap


class SpritesService {
    private fun addMascot(mascotDBHelper: MascotDBHelper, index: Int) {
        val hashMap: HashMap<Int, Bitmap> = mascotDBHelper.getMascotAssets(index, SpriteUtil.getUsedSprites())
        if (hashMap.isNotEmpty()) cachedSprites[index] =
            resizeSprites(Sprites(hashMap), sizeMultiplier)
    }

    private fun invalidateSprites(sprites: List<Int>) {
        val hashSet1 = HashSet(sprites)
        val hashSet = HashSet(cachedSprites.keys)
        hashSet.removeAll(hashSet1)
        for (index in hashSet) {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Invalidating id: ")
            stringBuilder.append(index)
            Log.d("SHIMEJI", stringBuilder.toString())
            cachedSprites[index]?.recycle()
            cachedSprites.remove(index)
        }
    }

    fun getSpritesById(context: Context, index: Int): Sprites {
        if (!cachedSprites.containsKey(index) || cachedSprites[index] == null) {
            val mascotDBHelper = MascotDBHelper(context)
            addMascot(mascotDBHelper, index)
            mascotDBHelper.close()
        }
        return cachedSprites[index]!!
    }

    fun loadSpritesForMascots(context: Context, paramList: List<Int>) {
        invalidateSprites(paramList)
        val mascotDBHelper = MascotDBHelper(context)
        for (integer in paramList) {
            if (!cachedSprites.containsKey(integer)){
                addMascot(mascotDBHelper, integer)
            }
        }
        mascotDBHelper.close()
    }

    fun setSizeMultiplier(context: Context, paramDouble: Double) {
        if (paramDouble != sizeMultiplier) {
            sizeMultiplier = paramDouble
            if (!cachedSprites.isEmpty()) {
                val mascotDBHelper = MascotDBHelper(context)
                for (integer in cachedSprites.keys) {
                    val hashMap: HashMap<Int, Bitmap> =
                        mascotDBHelper.getMascotAssets(integer, SpriteUtil.getUsedSprites())
                    cachedSprites[integer] = resizeSprites(Sprites(hashMap), sizeMultiplier)
                }
                mascotDBHelper.close()
            }
        }
    }

    companion object {
        private val cachedSprites: ConcurrentHashMap<Int, Sprites> = ConcurrentHashMap<Int, Sprites>()
        val instance: SpritesService by lazy { SpritesService() }
        private var sizeMultiplier = 1.0
    }
}
