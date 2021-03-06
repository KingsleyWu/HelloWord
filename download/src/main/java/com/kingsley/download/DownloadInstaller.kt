package com.kingsley.download

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.ProcessLifecycleOwner
import com.kingsley.common.lifecycle.AppLifeObserver

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/14
 * 描述　:
 */
val appContext: Application by lazy { DownloadInstaller.app }

class DownloadInstaller : ContentProvider() {

    companion object {
        lateinit var app: Application
        var watchAppLife = true
    }

    override fun onCreate(): Boolean {
        val application = context!!.applicationContext as Application
        install(application)
        return true
    }

    private fun install(application: Application) {
        app = application
        if (watchAppLife) ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifeObserver)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null


    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}