package com.kingsley.shimeji.data

import android.database.sqlite.SQLiteDatabase

import android.graphics.Bitmap

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.kingsley.shimeji.data.Helper.bitmapToByteArray
import com.kingsley.shimeji.data.Helper.byteArrayToBitmap
import com.kingsley.shimeji.mascotlibrary.MascotListing
import java.lang.Exception
import java.lang.StringBuilder


class MascotDBHelper internal constructor(context: Context) :
    SQLiteAssetHelper(context, "Mascots.db", null, 1) {
    fun addMascotToDatabase(paramInt: Int, paramString: String?, paramList: List<Bitmap?>) {
        val sQLiteDatabase: SQLiteDatabase = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("_id", Integer.valueOf(paramInt))
        contentValues.put("name", paramString)
        contentValues.put("purchased", Integer.valueOf(1))
        sQLiteDatabase.beginTransaction()
        try {
            sQLiteDatabase.insertWithOnConflict("mascots", null, contentValues, 2)
            for (b in paramList.indices) {
                val contentValues1 = ContentValues()
                contentValues1.put("mascot", Integer.valueOf(paramInt))
                contentValues1.put("frame", Integer.valueOf(b))
                contentValues1.put("bitmap", bitmapToByteArray(paramList[b]!!))
                sQLiteDatabase.insert("bitmaps", null, contentValues1)
            }
            sQLiteDatabase.setTransactionSuccessful()
            sQLiteDatabase.endTransaction()
            sQLiteDatabase.close()
        } catch (exception: Exception) {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Shimeji Insert Transaction rollback because: ")
            stringBuilder.append(exception.message)
            Log.d("SHIMEJI", stringBuilder.toString())
            sQLiteDatabase.endTransaction()
            sQLiteDatabase.close()
        } finally {
        }
    }

    fun getMascotAssets(paramInt: Int, indexSet: HashSet<Int>): HashMap<Int, Bitmap> {
        val hashMap = HashMap<Int, Bitmap>()
        val sQLiteDatabase: SQLiteDatabase = readableDatabase
        val cursor: Cursor = sQLiteDatabase.query(
            "bitmaps",
            arrayOf("bitmap"),
            "mascot = ?",
            arrayOf(paramInt.toString()),
            null,
            null,
            "frame ASC"
        )
        var index = 0
        if (cursor.moveToFirst()) while (!cursor.isAfterLast) {
            if (indexSet.contains(index)) hashMap[index] =
                byteArrayToBitmap(cursor.getBlob(cursor.getColumnIndexOrThrow("bitmap")))
            index += 1
            cursor.moveToNext()
        }
        cursor.close()
        sQLiteDatabase.close()
        return hashMap
    }

    val mascotThumbnails: MutableList<MascotListing>
        get() {
            val arrayList: ArrayList<MascotListing> = ArrayList()
            val sQLiteDatabase: SQLiteDatabase = readableDatabase
            val cursor: Cursor = sQLiteDatabase.rawQuery(
                "Select M._id,M.name,B.bitmap from mascots as M INNER JOIN bitmaps as B ON M._ID=B.mascot WHERE B.frame=0",
                null
            )
            if (cursor.moveToFirst()) while (!cursor.isAfterLast) {
                val mascotListing = MascotListing()
                mascotListing.id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                mascotListing.name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                mascotListing.thumbnail = byteArrayToBitmap(cursor.getBlob(cursor.getColumnIndexOrThrow("bitmap")))
                arrayList.add(mascotListing)
                cursor.moveToNext()
            }
            cursor.close()
            sQLiteDatabase.close()
            return arrayList
        }

    fun removeMascotFromDatabase(paramInt: Int) {
        val sQLiteDatabase: SQLiteDatabase = writableDatabase
        val arrayOfString = arrayOfNulls<String>(1)
        arrayOfString[0] = paramInt.toString()
        sQLiteDatabase.beginTransaction()
        try {
            sQLiteDatabase.delete("mascots", "_id=?", arrayOfString)
            sQLiteDatabase.delete("bitmaps", "mascot=?", arrayOfString)
            sQLiteDatabase.setTransactionSuccessful()
            sQLiteDatabase.endTransaction()
        } catch (exception: Exception) {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Could not delete mascot because:")
            stringBuilder.append(exception.message)
            Log.e("SHIMEJI", stringBuilder.toString())
            sQLiteDatabase.endTransaction()
        } finally {
        }
        sQLiteDatabase.close()
    }

    companion object {
        private const val DATABASE_NAME = "Mascots.db"
        private const val DATABASE_VERSION = 1
    }
}
