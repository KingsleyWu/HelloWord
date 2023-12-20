package com.kingsley.helloword.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileNotFoundException
import java.util.Objects

class MyFileProvider : ContentProvider() {

    companion object {
        private var mAuthority: String? = null

        fun getUriForFile(file: File): Uri {
            return Uri.Builder().scheme("content").authority(mAuthority).encodedPath(file.path).build()
        }
    }

    override fun attachInfo(context: Context?, info: ProviderInfo) {
        super.attachInfo(context, info) // Sanity check our security
        if (info.exported) {
            throw SecurityException("Provider must not be exported")
        }
        if (!info.grantUriPermissions) {
            throw SecurityException("Provider must grant uri permissions")
        }
        mAuthority = info.authority
    }


    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        throw UnsupportedOperationException("Not supported by this provider")
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("Not supported by this provider")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Not supported by this provider")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Not supported by this provider")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("Not supported by this provider")
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val file = File(Objects.requireNonNull(uri.path))
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    }

}