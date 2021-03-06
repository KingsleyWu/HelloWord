package com.kingsley.shimeji.data

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.StringBuilder
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.collections.ArrayList

internal object Utils {
    private val TAG = SQLiteAssetHelper::class.java.simpleName
    fun splitSqlScript(script: String, delim: Char): List<String> {
        val statements: MutableList<String> = ArrayList()
        var sb = StringBuilder()
        var inLiteral = false
        val content = script.toCharArray()
        for (index in script.indices) {
            if (content[index] == '"') {
                inLiteral = !inLiteral
            }
            if (content[index] == delim && !inLiteral) {
                if (sb.isNotEmpty()) {
                    statements.add(sb.toString().trim { it <= ' ' })
                    sb = StringBuilder()
                }
            } else {
                sb.append(content[index])
            }
        }
        if (sb.isNotEmpty()) {
            statements.add(sb.toString().trim { it <= ' ' })
        }
        return statements
    }

    @Throws(IOException::class)
    fun writeExtractedFileToDisk(inputStream: InputStream, outs: OutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outs.write(buffer, 0, length)
        }
        outs.flush()
        outs.close()
        inputStream.close()
    }

    @Throws(IOException::class)
    fun getFileFromZip(zipFileStream: InputStream?): ZipInputStream? {
        val zis = ZipInputStream(zipFileStream)
        var ze: ZipEntry
        while (zis.nextEntry.also { ze = it } != null) {
            Log.w(TAG, "extracting file: '" + ze.name.toString() + "'...")
            return zis
        }
        return null
    }

    fun convertStreamToString(inputStream: InputStream?): String {
        return Scanner(inputStream).useDelimiter("\\A").next()
    }
}