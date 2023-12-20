package com.kingsley.download.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kingsley.download.bean.DownloadInfo.Status.DOWNLOADING
import com.kingsley.download.bean.DownloadInfo.Status.PAUSE
import com.kingsley.download.bean.DownloadInfo.Status.PAUSED
import com.kingsley.download.bean.DownloadInfo.Status.PENDING
import com.kingsley.download.bean.DownloadInfo.Status.WAITING
import com.kingsley.download.bean.DownloadGroup
import com.kingsley.download.dao.DownloadDao
import com.kingsley.download.utils.DownloadDBUtil

@Database(entities = [DownloadGroup::class], version = 1, exportSchema = false)
abstract class DownloadDatabase : RoomDatabase() {

    abstract fun downloadDao(): DownloadDao

    companion object {
        @Volatile
        private var INSTANCE: DownloadDatabase? = null

        fun getInstance(context: Context): DownloadDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, DownloadDatabase::class.java, DownloadDBUtil.DB_NAME)
                .allowMainThreadQueries()
                .addCallback(callback).build()

        private val callback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                //fix abnormal exit state
                fixAbnormalState(db)
            }
        }
    }
}

internal fun fixAbnormalState(db: SupportSQLiteDatabase) {
    db.beginTransaction()
    try {
        db.execSQL("""UPDATE ${DownloadDBUtil.DOWNLOAD_TABLE_NAME} SET status = $PAUSED, abnormalExit = "1" WHERE status = $WAITING""")
        db.execSQL("""UPDATE ${DownloadDBUtil.DOWNLOAD_TABLE_NAME} SET status = $PAUSED, abnormalExit = "0" WHERE status = $PAUSE""")
        db.execSQL("""UPDATE ${DownloadDBUtil.DOWNLOAD_TABLE_NAME} SET status = $PAUSED, abnormalExit = "1" WHERE status = $DOWNLOADING""")
        db.execSQL("""UPDATE ${DownloadDBUtil.DOWNLOAD_TABLE_NAME} SET status = $PAUSED, abnormalExit = "1" WHERE status = $PENDING""")
        db.setTransactionSuccessful()
    } finally {
        db.endTransaction()
    }
}