package com.kingsley.helloword.splitapk

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageInstaller.SessionParams
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import androidx.lifecycle.viewModelScope
import com.kingsley.base.BaseViewModel
import com.kingsley.common.L
import com.kingsley.helloword.provider.MyFileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class SplitApkInstallViewModel : BaseViewModel() {

    private val fileNames = listOf("base-2488.apk", "split-2488-0.apk", "split-2488-1.apk")
    // context.getAssets().open(fileName)

    fun installWithNative(context: Context) {
        try {
            val sourcePath = context.externalCacheDir!!.absolutePath + File.separator + fileNames[1]
            val sourceFile = File(sourcePath)
            L.d("sourcePath = $sourcePath")
            if (sourceFile.exists()) {
                context.startActivity(getInstallApkIntent(sourceFile))
            }
            L.d("sourceFile.exists() = ${sourceFile.exists()}")
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun installWithSession(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val packageInstaller = context.packageManager.packageInstaller
            val sessionParams = SessionParams(SessionParams.MODE_FULL_INSTALL)
            var session: PackageInstaller.Session? = null
            val exception: java.lang.Exception? = null
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    sessionParams.setRequireUserAction(SessionParams.USER_ACTION_NOT_REQUIRED)
                }
                val sessionID = packageInstaller.createSession(sessionParams)
                session = packageInstaller.openSession(sessionID)
                val path = context.externalCacheDir!!.absolutePath + File.separator
                for (apkFileName in fileNames) {
                    val apkFile = File(path + apkFileName)
                    var inputStream: InputStream? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val uri = MyFileProvider.getUriForFile(apkFile)
                        inputStream = context.contentResolver.openInputStream(uri)
                    }
                    if (inputStream == null) {
                        inputStream = FileInputStream(apkFile)
                    }
                    val outputStream = session.openWrite(apkFile.name, 0, apkFile.length())
//                    FileUtils.copyStream(inputStream, outputStream)
                    session.fsync(outputStream)
                    inputStream.close()
                    outputStream.close()
                }
            } catch (e: Exception) {

            }

        }
    }

    private fun getInstallApkIntent(apkFile: File): Intent {
        val installIntent = Intent(Intent.ACTION_VIEW)
        val dataType = "application/vnd.android.package-archive"
        //Android7.0之后获取uri要用contentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val uri: Uri = MyFileProvider.getUriForFile(apkFile)
            installIntent.setDataAndType(uri, dataType)
            installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            //表示对目标应用临时授权该Uri所代表的文件
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            installIntent.setDataAndType(Uri.fromFile(apkFile), dataType)
            installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return installIntent
    }


    private fun copy(fis: InputStream?, target: File?): Boolean {
        if (target == null || fis == null) {
            return false
        }
        var fos: FileOutputStream? = null
        return try {
            createDipPath(target.path)
            fos = FileOutputStream(target)
            val buffer = ByteArray(1024)
            var read = -1
            while (fis.read(buffer).also { read = it } != -1) {
                fos.write(buffer, 0, read)
            }
            fos.flush()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                    fis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun createDipPath(file: String) {
        if (!file.contains("/")) {
            return
        }
        val parentFile = file.substring(0, file.lastIndexOf("/"))
        val temp = File(file)
        val parent = File(parentFile)
        if (!temp.exists()) {
            parent.mkdirs()
            try {
                temp.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}