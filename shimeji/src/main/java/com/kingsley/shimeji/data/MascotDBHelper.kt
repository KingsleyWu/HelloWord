package com.kingsley.shimeji.data

import android.database.sqlite.SQLiteDatabase

import android.graphics.Bitmap

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.kingsley.shimeji.mascotlibrary.MascotListing
import java.lang.Exception
import java.lang.StringBuilder


class MascotDBHelper internal constructor(context: Context) :
    SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    fun addMascotToDatabase(mascotId: Int, mascotName: String?, mascotBitmaps: List<Bitmap?>) {
        val sQLiteDatabase: SQLiteDatabase = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("_id", mascotId)
        contentValues.put(MascotEntry.COLUMN_NAME_NAME, mascotName)
        contentValues.put(MascotEntry.COLUMN_NAME_PURCHASED, 1)
        sQLiteDatabase.beginTransaction()
        try {
            sQLiteDatabase.insertWithOnConflict(MascotEntry.TABLE_NAME, null, contentValues, 2)
            for (index in mascotBitmaps.indices) {
                val mascotContentValues = ContentValues()
                mascotContentValues.put(BitmapEntry.COLUMN_NAME_MASCOT_ID, mascotId)
                mascotContentValues.put(BitmapEntry.COLUMN_NAME_FRAME, index)
                mascotContentValues.put(BitmapEntry.COLUMN_NAME_BITMAP,
                    mascotBitmaps[index]?.let { Helper.bitmapToByteArray(it) })
                sQLiteDatabase.insert(BitmapEntry.TABLE_NAME, null, mascotContentValues)
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

    fun getMascotAssets(key: Int, indexSet: HashSet<Int>): HashMap<Int, Bitmap> {
        val hashMap = HashMap<Int, Bitmap>()
        val sQLiteDatabase: SQLiteDatabase = readableDatabase
        val cursor: Cursor = sQLiteDatabase.query(
            BitmapEntry.TABLE_NAME,
            arrayOf(BitmapEntry.COLUMN_NAME_BITMAP),
            "${BitmapEntry.COLUMN_NAME_MASCOT_ID} = ?",
            arrayOf(key.toString()),
            null,
            null,
            "${BitmapEntry.COLUMN_NAME_FRAME} ASC"
        )
        var index = 0
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                if (indexSet.contains(index)) hashMap[index] =
                    Helper.byteArrayToBitmap(cursor.getBlob(cursor.getColumnIndexOrThrow(BitmapEntry.COLUMN_NAME_BITMAP)))
                index += 1
                cursor.moveToNext()
            }
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
                "Select M._id,M.${MascotEntry.COLUMN_NAME_NAME},B.${BitmapEntry.COLUMN_NAME_BITMAP} from ${MascotEntry.TABLE_NAME} as M INNER JOIN ${BitmapEntry.TABLE_NAME} as B ON M._ID=B.${BitmapEntry.COLUMN_NAME_MASCOT_ID} WHERE B.${BitmapEntry.COLUMN_NAME_FRAME}=0",
                null
            )
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val mascotListing = MascotListing()
                    mascotListing.id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                    mascotListing.name = cursor.getString(cursor.getColumnIndexOrThrow(MascotEntry.COLUMN_NAME_NAME))
                    mascotListing.thumbnail = Helper.byteArrayToBitmap(cursor.getBlob(cursor.getColumnIndexOrThrow(BitmapEntry.COLUMN_NAME_BITMAP)))
                    arrayList.add(mascotListing)
                    cursor.moveToNext()
                }
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
            sQLiteDatabase.delete(MascotEntry.TABLE_NAME, "_id=?", arrayOfString)
            sQLiteDatabase.delete(BitmapEntry.TABLE_NAME, "${BitmapEntry.COLUMN_NAME_MASCOT_ID}=?", arrayOfString)
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
