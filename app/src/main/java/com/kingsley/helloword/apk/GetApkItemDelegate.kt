package com.kingsley.helloword.apk

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.kingsley.base.adapter.ItemViewDelegate
import com.kingsley.base.adapter.BaseViewHolder
import com.kingsley.base.showShort
import com.kingsley.helloword.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel


/**
 * @author Kingsley
 * Created on 2021/6/24.
 */
class GetApkItemDelegate(val requestPermission: ActivityResultLauncher<Array<String>>) :
    ItemViewDelegate<PackageInfo, GetApkItemDelegate.ViewHolder>() {

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) = ViewHolder(parent)

    inner class ViewHolder(parent: ViewGroup) :
        BaseViewHolder<PackageInfo>(parent, R.layout.get_apk_item) {
        private val backupPath =
            (mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path
                ?: ((mContext.externalCacheDir?.path
                    ?: mContext.filesDir.path) + File.separator + "Download")) + File.separator
        private val mIvApkIcon: ImageView by lazy { findViewById(R.id.iv_apk_icon) }
        private val mTvApkName: TextView by lazy { findViewById(R.id.tv_apk_name) }
        private val mTvApkPackageId: TextView by lazy { findViewById(R.id.tv_apk_package_id) }
        private val mPackageManager: PackageManager by lazy { mContext.packageManager }

        override fun setData(data: PackageInfo) {
            val packageId = data.packageName
            val appName = data.applicationInfo.loadLabel(mPackageManager).toString()
            mIvApkIcon.setImageDrawable(data.applicationInfo.loadIcon(mPackageManager))
            mTvApkPackageId.text = packageId
            mTvApkName.text = appName
            itemView.setOnClickListener {
                val intent = mPackageManager.getLaunchIntentForPackage(packageId)
                if (intent == null) {
                    mContext.showShort("未安装")
                } else {
                    if (mContext !is Activity) {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    mContext.startActivity(intent)
                }
            }

            itemView.setOnLongClickListener {
                if (check()) {
                    showDialog(data, appName, packageId)
                } else {
                    requestPermission.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }
                return@setOnLongClickListener true
            }
        }

        private fun check(): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
            return true
        }

        private fun showDialog(packageInfo: PackageInfo, appName: String, packageId: String) {
            val builder = AlertDialog.Builder(mContext, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            builder.setTitle(R.string.dialog_title)
                .setMessage(appName + "\n" + packageId)
                .setPositiveButton(R.string.positive_btn) { dialog, which ->
                    val dest = doCopy(packageInfo, appName)
                    showSnackBar(dest, itemView) {
                        share(dest, "eeeee")
                    }
                }
                .setNegativeButton(R.string.negative_btn) { dialog, which ->

                }.show()
        }

        private fun showSnackBar(
            message: String,
            holderView: View,
            listener: View.OnClickListener
        ) {
            Snackbar.make(holderView, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_btn, listener).show()
        }

        private fun doCopy(packageInfo: PackageInfo, appName: String): String {
            val source = packageInfo.applicationInfo.sourceDir
            val dest = "$backupPath$appName.apk"
            try {
                if (!File(backupPath).exists()) {
                    File(backupPath).mkdir()
                }

                val destFile = File(dest)
                if (destFile.exists()) {
                    destFile.delete()
                }
                destFile.createNewFile()
                val ins = FileInputStream(File(source))
                val out = FileOutputStream(destFile)
                val inC: FileChannel = ins.channel
                val outC: FileChannel = out.channel
                var length: Long
                while (true) {
                    if (inC.position() == inC.size()) {
                        inC.close()
                        outC.close()
                        break
                    }
                    length = if (inC.size() - inC.position() < 1024 * 1024) {
                        (inC.size() - inC.position())
                    } else {
                        1024 * 1024
                    }
                    inC.transferTo(inC.position(), length, outC)
                    inC.position(inC.position() + length)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return dest
        }

        private fun share(filePath: String, title: String) {
            val intent = Intent(Intent.ACTION_SEND)
            val uriForFile = FileProvider.getUriForFile(
                mContext,
                "com.kingsley.helloword.FileProvider",
                File(filePath)
            )
            intent.putExtra(Intent.EXTRA_STREAM, uriForFile)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "application/octet-stream"

            val chooser = Intent.createChooser(intent, title).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            //grantUriPermission(mContext, chooser, uriForFile)
            mContext.startActivity(chooser)
        }

        private fun grantUriPermission(context: Context, it: Intent, uri: Uri) {
            if (it.resolveActivity(context.packageManager) != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val resInfoList = context.packageManager
                    .queryIntentActivities(it, PackageManager.MATCH_DEFAULT_ONLY)
                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    context.grantUriPermission(
                        packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
            }
        }
    }
}
