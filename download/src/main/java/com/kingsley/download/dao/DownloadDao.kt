package com.kingsley.download.dao

import androidx.room.*
import com.kingsley.download.bean.DownloadInfo

@Dao
interface DownloadDao {

    /**
     * 获取所有
     */
    @Query("SELECT * FROM DownloadInfo")
    suspend fun queryAll(): MutableList<DownloadInfo>

    /**
     * 通过状态查询任务
     */
    @Query("SELECT * FROM DownloadInfo WHERE status = :status")
    suspend fun queryByStatus(status: Int): MutableList<DownloadInfo>

    /**
     * 查询正在下载的任务
     */
    @Query("SELECT * FROM DownloadInfo WHERE status != ${DownloadInfo.DONE}")
    suspend fun queryLoading(): MutableList<DownloadInfo>

    /**
     * 查询正在下载的任务的url
     */
    @Query("SELECT url FROM DownloadInfo WHERE status != ${DownloadInfo.DONE}")
    suspend fun queryLoadingUrls(): MutableList<String>

    /**
     * 查询下载完成的任务
     */
    @Query("SELECT * FROM DownloadInfo WHERE status == ${DownloadInfo.DONE}")
    suspend fun queryDone(): MutableList<DownloadInfo>

    /**
     * 通過id查询下载完成的任务
     */
    @Query("SELECT * FROM DownloadInfo WHERE `group` = :group")
    suspend fun queryByGroup(group: String): MutableList<DownloadInfo>

    /**
     * 查询下载完成的任务的url
     */
    @Query("SELECT url FROM DownloadInfo WHERE status == ${DownloadInfo.DONE}")
    suspend fun queryDoneUrls(): MutableList<String>

    /**
     * 通过 type 查询
     */
    @Query("SELECT * FROM DownloadInfo WHERE type = :type")
    suspend fun queryByType(type: String): MutableList<DownloadInfo>

    /**
     * 通过 flag 查询
     */
    @Query("SELECT * FROM DownloadInfo WHERE flag = :flag")
    suspend fun queryByFlag(flag: String): MutableList<DownloadInfo>

    /**
     * 通过url查询,每一个任务他们唯一的标志就是url
     */
    @Query("SELECT * FROM DownloadInfo WHERE url LIKE :url")
    suspend fun queryByUrl(url: String): DownloadInfo?

    /**
     * 插入或替换
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(vararg downloadData: DownloadInfo): List<Long>

    /**
     * 删除
     */
    @Delete
    suspend fun delete(downloadData: DownloadInfo): Int

    /**
     * 删除
     */
    @Delete
    suspend fun delete(vararg downloadData: DownloadInfo): Int

    /**
     * 删除
     */
    @Delete
    suspend fun delete(downloadData: List<DownloadInfo>): Int

    /**
     * 删除
     */
    @Query("DELETE FROM DownloadInfo WHERE url = :url")
    suspend fun deleteByUrl(url: String): Int

    /**
     * 删除
     */
    @Query("DELETE FROM DownloadInfo WHERE url IN (:urls)")
    suspend fun deleteByUrls(urls: List<String>): Int

    /**
     * 删除
     */
    @Query("DELETE FROM DownloadInfo WHERE 'group' = :group")
    suspend fun deleteByGroup(group: String): Int

    /**
     * 删除
     */
    @Query("DELETE FROM DownloadInfo WHERE 'group' IN (:groups)")
    suspend fun deleteByGroups(groups: List<String>): Int

}