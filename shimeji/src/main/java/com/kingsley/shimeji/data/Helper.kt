package com.kingsley.shimeji.data

import android.content.Context
import android.content.SharedPreferences

import android.graphics.Bitmap

import com.kingsley.shimeji.mascot.Sprites

import org.json.JSONException

import org.json.JSONArray

import androidx.core.content.ContextCompat

import android.graphics.drawable.BitmapDrawable

import android.graphics.drawable.Drawable

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import com.kingsley.shimeji.AppConstants
import com.kingsley.shimeji.mascotlibrary.MascotListing
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder


object Helper {
    fun bitmapToByteArray(paramBitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun byteArrayToBitmap(arrayOfByte: ByteArray?): Bitmap {
        return BitmapFactory.decodeStream(ByteArrayInputStream(arrayOfByte))
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) return drawable.bitmap
        val bitmap =
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun getActiveTeamMembers(context: Context): List<Int> {
        val arrayList: ArrayList<Int> = ArrayList()
//        val arrayOfString: List<String> =
//            context.getSharedPreferences(AppConstants.MY_PREFS, Context.MODE_MULTI_PROCESS)
//                .getString(AppConstants.ACTIVE_MASCOTS_IDS, "")
//                ?.split(",")!!
//        var i = arrayOfString.size
//        var b: Byte = 0
//        if (i != 1 || arrayOfString[0] != "") {
//            i = arrayOfString.size
//            while (b < i) {
//                arrayList.add(Integer.valueOf(arrayOfString[b.toInt()].toInt()))
//                b++
//            }
//        }
        arrayList.add(1)
        arrayList.add(2)
        return arrayList
    }

    fun getNotificationVisibility(context: Context): Boolean {
        return context.getSharedPreferences(AppConstants.MY_PREFS, Context.MODE_MULTI_PROCESS)
            .getBoolean(AppConstants.SHOW_NOTIFICATION, AppConstants.DEFAULT_SHOW_NOTIFICATION)
    }

    fun getReappearDelayMs(context: Context): Long {
        return (context.getSharedPreferences(AppConstants.MY_PREFS, Context.MODE_MULTI_PROCESS)
            .getString(AppConstants.REAPPEAR_DELAY, AppConstants.DEFAULT_REAPPEAR_DELAY_MINUTES)?.toLong() ?: 0) * 1000 * 60
    }

    private fun getResizedBitmap(bitmap: Bitmap, scale: Float): Bitmap {
        val i = bitmap.width
        val j = bitmap.height
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(bitmap, 0, 0, i, j, matrix, false)
    }

    fun getResizedBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val i = bitmap.width
        val j = bitmap.height
        val f1 = (width / i).toFloat()
        val f2 = (height / j).toFloat()
        val matrix = Matrix()
        if (f1 > f2) {
            matrix.postScale(f1, f1)
        } else {
            matrix.postScale(f2, f2)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, i, j, matrix, false)
    }

    fun getSizeMultiplier(paramContext: Context): Double {
        return paramContext.getSharedPreferences(AppConstants.MY_PREFS, Context.MODE_MULTI_PROCESS)
            .getString(AppConstants.SIZE_MULTIPLIER, AppConstants.DEFAULT_SIZE_MULTIPLIER)?.toDouble()!!
    }

    fun getSpeedMultiplier(context: Context): Double {
        return context.getSharedPreferences(AppConstants.MY_PREFS, Context.MODE_MULTI_PROCESS)
            .getString(AppConstants.ANIMATION_SPEED, AppConstants.DEFAULT_ANIMATION_SPEED)?.toDouble()!!
    }
//
//    fun makeRateDialog(context: Context): RateMeDialog {
//        return Builder(
//            context.packageName,
//            MainActivity.getApplicationName(context)
//        ).enableFeedbackByEmail(AppConstants.DEVELOPER_EMAIL).showAppIcon(2131492864)
//            .setBodyTextColor(ContextCompat.getColor(context, 2131034154))
//            .setBodyBackgroundColor(ContextCompat.getColor(context, 2131034157))
//            .setHeaderBackgroundColor(ContextCompat.getColor(context, 2131034154))
//            .setRateButtonBackgroundColor(ContextCompat.getColor(context, 2131034154))
//            .setRateButtonPressedBackgroundColor(ContextCompat.getColor(context, 2131034155)).build()
//    }

    fun notifyBackgroundChanged(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(AppConstants.MY_PREFS, Context.MODE_MULTI_PROCESS)
        val editor = sharedPreferences.edit()
        val i = sharedPreferences.getInt(AppConstants.UPDATE_EVENT_TOKEN, 0)
        editor.putInt(AppConstants.UPDATE_EVENT_TOKEN, i + 1)
        editor.apply()
    }

    @Throws(JSONException::class)
    fun parseJSON(paramJSONArray: JSONArray): List<MascotListing> {
        val arrayList: ArrayList<MascotListing> = ArrayList()
        for (b in 0 until paramJSONArray.length()) {
            val jSONObject = paramJSONArray.getJSONObject(b)
            val mascotListing = MascotListing()
            mascotListing.id = jSONObject.getInt("id")
            mascotListing.name = jSONObject.getString("name")
            mascotListing.author = jSONObject.getString("author")
            mascotListing.category = jSONObject.getString("category")
            arrayList.add(mascotListing)
        }
        return arrayList
    }

    fun resizeSprites(paramSprites: Sprites, paramDouble: Double): Sprites {
        for (integer in paramSprites.keys) paramSprites[integer] = getResizedBitmap(
            paramSprites[integer]!!,
            paramDouble.toFloat()
        )
        return paramSprites
    }

    fun saveActiveTeamMembers(paramContext: Context, paramList: List<Int?>) {
        val editor: SharedPreferences.Editor = paramContext.getSharedPreferences(AppConstants.MY_PREFS, Context.MODE_MULTI_PROCESS).edit()
        val stringBuilder = StringBuilder()
        val iterator = paramList.iterator()
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next())
            stringBuilder.append(",")
        }
        editor.putString(AppConstants.ACTIVE_MASCOTS_IDS, stringBuilder.toString())
        editor.apply()
    }
}
