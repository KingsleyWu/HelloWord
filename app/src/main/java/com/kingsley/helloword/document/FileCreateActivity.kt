package com.kingsley.helloword.document

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.kingsley.common.L
import com.kingsley.base.activity.BaseVbActivity
import com.kingsley.helloword.databinding.FileCreateActivityBinding
import com.tencent.mmkv.MMKV
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter


class FileCreateActivity : BaseVbActivity<FileCreateActivityBinding>() {
    companion object{
        const val URI_PATH = "URI_PATH"
    }
    private lateinit var mMMKV: MMKV

    private var mCreateDocumentLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                // writeInFile(it.data?.data, "bison is bision");
                it.data?.data?.let { uri ->
                    L.d("wwc uri = $uri")
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    val docFile = DocumentFile.fromTreeUri(this, uri)
                    docFile?.createDirectory("HelloWord")

                    L.d("wwc docFile = ${docFile?.listFiles()}")
                    mMMKV.encode(URI_PATH, uri)
                    Toast.makeText(
                        this,
                        "canRead: ${docFile?.canRead()} canWrite: ${docFile?.canWrite()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                L.d("wwc it.resultCode = ${it.resultCode}")
            }
        }

    private fun writeInFile(uri: Uri?, text: String) {
        uri?.let {
            val outputStream: OutputStream?
            try {
                outputStream = contentResolver.openOutputStream(uri)
                val bw = BufferedWriter(OutputStreamWriter(outputStream))
                bw.write(text)
                bw.flush()
                bw.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // create text file
    private fun createFile() {
        // when you create document, you need to add Intent.ACTION_CREATE_DOCUMENT
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

        // filter to only show openable items.
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Create a file with the requested Mime type
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TITLE, "Neonankiti.txt")
        mCreateDocumentLauncher.launch(intent)
    }

    // create text file
    private fun createOpenDocumentTreeIntent(uriString: String?) {
        val storageManager = this.getSystemService(Context.STORAGE_SERVICE) as? StorageManager

        if (uriString == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val createOpenDocumentTreeIntent = storageManager?.primaryStorageVolume?.createOpenDocumentTreeIntent()
                createOpenDocumentTreeIntent?.let {
                    mCreateDocumentLauncher.launch(it)
                }
            } else {
                val intent = Intent("android.intent.action.OPEN_DOCUMENT_TREE")
                if (Build.VERSION.SDK_INT >= 26) {
                    // apparently we need a valid content URI
                    val uri =
                        Uri.parse("content://com.android.externalstorage.documents/document/primary%3ADocuments")
                    intent.putExtra("android.provider.extra.INITIAL_URI", uri)
                }
                mCreateDocumentLauncher.launch(intent)
            }
        } else {
            val uri = Uri.parse(uriString)
            val docFile = DocumentFile.fromTreeUri(this, uri)

            Toast.makeText(
                this,
                "canRead: ${docFile?.canRead()} canWrite: ${docFile?.canWrite()}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun initViewBinding(inflater: LayoutInflater) = FileCreateActivityBinding.inflate(inflater)

    override fun initView(savedInstanceState: Bundle?) {
        mMMKV = MMKV.defaultMMKV()
        mViewBind.root.setOnClickListener {
            val uriPath = mMMKV.decodeParcelable("URI_PATH", Uri::class.java)
            L.d("uriPath = $uriPath")
            if (uriPath == null) {
                createOpenDocumentTreeIntent(null)
            } else {
                val docFile = DocumentFile.fromTreeUri(this, uriPath)
                docFile?.listFiles()?.forEach {
                    L.d("wwc docName = ${it.name}, docUri = ${it.uri}")
                }
                Toast.makeText(
                    this,
                    "canRead: ${docFile?.canRead()} canWrite: ${docFile?.canWrite()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


}