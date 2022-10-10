package com.kingsley.helloword.keyboard

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class AttachmentsRepo(private val mContext: Context) {
    // This matches the name declared in AndroidManifest.xml
    private val FILE_PROVIDER_AUTHORITY: String = mContext.packageName + ".FileProvider"
    private val mAttachmentsDir: File = File(mContext.filesDir, "attachments")

    /**
     * Reads the content at the given URI and writes it to private storage. Then returns a content
     * URI referencing the newly written file.
     */
    fun write(uri: Uri): Uri {
        val contentResolver = mContext.contentResolver
        val mimeType = contentResolver.getType(uri)
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        try {
            contentResolver.openInputStream(uri).use { `is` ->
                requireNotNull(`is`) { uri.toString() }
                mAttachmentsDir.mkdirs()
                val fileName = "a-" + UUID.randomUUID().toString() + "." + ext
                val newAttachment = File(mAttachmentsDir, fileName)
                FileOutputStream(newAttachment).use { os ->
                    var length: Int
                    val bytes = ByteArray(1024)
                    // copy data from input stream to output stream
                    while (`is`.read(bytes).also { length = it } != -1) {
                        os.write(bytes, 0, length)
                    }
                }
                val resultUri = getUriForFile(newAttachment)
                Log.i("TAG", "Saved content: originalUri=$uri, resultUri=$resultUri")
                return resultUri
            }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    fun deleteAll() {
        val files = mAttachmentsDir.listFiles() ?: return
        for (file in files) {
            file.delete()
        }
    }

    private fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(mContext, FILE_PROVIDER_AUTHORITY, file)
    }

}