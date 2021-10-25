package com.kingsley.download.client

import androidx.room.Room
import androidx.room.migration.Migration
import com.kingsley.download.appContext
import com.kingsley.download.database.DownloadDatabase

object RoomClient {

    private const val DATA_BASE_NAME = "download.db"

    val dataBase: DownloadDatabase by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        Room
            .databaseBuilder(
                appContext,
                DownloadDatabase::class.java,
                DATA_BASE_NAME
            )
            .build()
    }

    private fun createMigrations(): Array<Migration> {
        return arrayOf()
    }

}