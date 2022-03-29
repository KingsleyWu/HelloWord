package com.kingsley.download.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.dao.DownloadDao

@Database(entities = [DownloadInfo::class], version = 1, exportSchema = false)
abstract class DownloadDatabase : RoomDatabase() {

    abstract fun downloadDao(): DownloadDao
}