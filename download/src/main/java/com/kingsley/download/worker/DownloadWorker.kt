package com.kingsley.download.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.kingsley.download.R
import java.util.*

class DownloadWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {

        return Result.success()
    }


    private suspend fun download(downloadUrl: String, outputFile: String, fileName: String) {
//        KCHttpV2.download(downloadUrl, "$outputFile/$fileName", onProcess = { _, _, process ->
//            setForeground(createForegroundInfo("${(process * 100).toInt()}%"))
//            setProgress(Data.Builder().let {
//                it.putInt("progress", (process * 100).toInt())
//                it.build()
//            })
//        }, onSuccess = {
//            setForeground(createForegroundInfo("download ok"))
//        },onError = {
//            it.printStackTrace()
//        })
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, getChannelID())
            .setContentTitle(getTitle())
            .setTicker(getTitle())
            .setContentText(progress)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setOngoing(true) //防止滑动删除
//            .addAction(R.mipmap.picture_icon_delete_photo , "取消", intent)
            .build()

        return ForegroundInfo("下载文件".hashCode(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        if (NotificationManagerCompat.from(applicationContext).getNotificationChannel(getChannelID()) == null) {
            val name = "文件下载"
            val description = "文件下载"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(getChannelID(), name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            return NotificationManagerCompat.from(applicationContext).createNotificationChannel(mChannel)
        }
    }

    private fun getTitle(): String {
        return "文件下载"
    }

    private fun getChannelID(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            "文件下载"
        } else {
            ""
        }
    }
    companion object {
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_OUT_PUT_URL = "KEY_OUT_URL"
        const val KEY_OUTPUT_FILE_NAME = "KEY_OUTPUT_FILE_NAME"

        fun startDownload(context: Context, downloadUrl: String, outputFile: String, fileName: String): UUID {
            val inputData: Data = Data.Builder().apply {
                putString(KEY_INPUT_URL, downloadUrl)
                putString(KEY_OUTPUT_FILE_NAME, fileName)
                putString(KEY_OUT_PUT_URL, outputFile)
            }.build()
            val request = OneTimeWorkRequestBuilder<DownloadWorker>().setInputData(inputData).build()
            WorkManager.getInstance(context).enqueue(request)
            return request.id
        }
    }
}