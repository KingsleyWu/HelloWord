package com.kingsley.download.dao

import androidx.room.*
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.bean.DownloadGroup

@Dao
interface DownloadDao {
    /**
     * 获取所有
     */
    @Query("SELECT * FROM download_record")
    fun queryAll(): List<DownloadGroup>

    /**
     * 通过状态查询任务
     */
    @Query("SELECT * FROM download_record WHERE status = :status")
    fun queryByStatus(status: Int): List<DownloadGroup>

    /**
     * 通过状态查询任务
     */
    @Query("SELECT * FROM download_record WHERE status IN (:status)")
    fun queryByStatus(status: List<Int>): List<DownloadGroup>

    /**
     * 通过状态查询任务
     */
    @Query("SELECT * FROM download_record WHERE status IN (:status)")
    fun queryByStatus(status: IntArray): List<DownloadGroup>

    /**
     * 查询正在下载的任务
     */
    @Query("SELECT * FROM download_record WHERE status != ${DownloadInfo.DONE}")
    fun queryLoading(): List<DownloadGroup>

    /**
     * 查询下载完成的任务
     */
    @Query("SELECT * FROM download_record WHERE status == ${DownloadInfo.DONE}")
    fun queryDone(): List<DownloadGroup>

    /**
     * 通过 id 查询任务
     */
    @Query("SELECT * FROM download_record WHERE id = :id")
    fun queryById(id: String): DownloadGroup?

    /**
     * 通过 id + type 查询任务
     */
    @Query("SELECT * FROM download_record WHERE id = :id AND type = :type")
    fun queryByIdAndType(id: String, type: String): DownloadGroup?

    /**
     * 通过 id + objId 查询任务
     */
    @Query("SELECT * FROM download_record WHERE id = :id AND objId = :objId")
    fun queryByIdAndObjId(id: String, objId: String): DownloadGroup?

    /**
     * 插入或替换
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(data: DownloadGroup): Long

    /**
     * 删除
     */
    @Delete
    fun delete(data: DownloadGroup): Int

    /**
     * 删除
     */
    @Query("DELETE FROM download_record WHERE id = :id")
    fun deleteById(id: String): Int

    /**
     * 通过 id 更新 data
     */
    @Query("UPDATE download_record SET data = :data WHERE id = :id")
    fun update(id: String, data: String): Int
}