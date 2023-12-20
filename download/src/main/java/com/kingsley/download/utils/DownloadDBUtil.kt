package com.kingsley.download.utils

import com.kingsley.download.appContext
import com.kingsley.download.bean.DownloadGroup
import com.kingsley.download.dao.DownloadDao
import com.kingsley.download.database.DownloadDatabase

internal object DownloadDBUtil {

    /** 數據庫名稱 */
    const val DB_NAME = "DownloadRecord.db"
    /** 表名 */
    const val DOWNLOAD_TABLE_NAME = "download_record"

    private val downloadDao: DownloadDao by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        DownloadDatabase.getInstance(appContext).downloadDao()
    }

    /**
     * 插入或替换
     */
    fun insertOrReplace(data: DownloadGroup): Long = downloadDao.insertOrReplace(data)

    /**
     * 删除
     */
    fun delete(data: DownloadGroup): Int = downloadDao.delete(data)

    /**
     * 删除
     */
    fun delete(id: String): Int = downloadDao.deleteById(id)

    /**
     * 获取所有
     */
    fun queryAll(): List<DownloadGroup> = downloadDao.queryAll()

    /**
     * 通过状态查询任务
     */
    fun queryByStatus(status: Int): List<DownloadGroup> = downloadDao.queryByStatus(status)

    /**
     * 通过状态查询任务
     */
    fun queryByStatus(status: List<Int>): List<DownloadGroup> = downloadDao.queryByStatus(status)

    /**
     * 通过状态查询任务
     */
    fun queryByStatus(vararg status: Int): List<DownloadGroup> = downloadDao.queryByStatus(status)

    /**
     * 查询正在下载的任务
     */
    fun queryLoading(): List<DownloadGroup> = downloadDao.queryLoading()

    /**
     * 查询下载完成的任务
     */
    fun queryDone(): List<DownloadGroup> = downloadDao.queryDone()

    /**
     * 通过 id 查询任务
     */
    fun queryById(id: String): DownloadGroup? = downloadDao.queryById(id)

    /**
     * 通过 id + objId 查询任务
     */
    fun queryByIdAndObjId(id: String, objId: String): DownloadGroup? = downloadDao.queryByIdAndObjId(id, objId)

    /**
     * 通过 id + type 查询任务
     */
    fun queryByIdAndType(id: String, type: String): DownloadGroup? = downloadDao.queryByIdAndType(id, type)

    /**
     * 通过 id 更新 data
     */
    fun update(id: String, data: String): Int = downloadDao.update(id, data)

}