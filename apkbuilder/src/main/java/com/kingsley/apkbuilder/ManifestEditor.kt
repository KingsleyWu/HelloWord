package com.kingsley.apkbuilder

import kotlinx.coroutines.NonCancellable.children
import org.apache.commons.io.IOUtils
import pxb.android.StringItem
import pxb.android.axml.AxmlReader
import pxb.android.axml.AxmlVisitor
import pxb.android.axml.AxmlWriter
import pxb.android.axml.NodeVisitor
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ManifestEditor(private val mManifestInputStream: InputStream) {
    private var mAppName: String? = null
    private var mManifestData: ByteArray
    private var mPackageName: String? = null
    private var mVersionCode = -1
    private var mVersionName: String? = null
    @Throws(IOException::class)
    fun commit(): ManifestEditor {
        val mutableAxmlWriter = MutableAxmlWriter()
        AxmlReader(
            IOUtils.readFully(
                mManifestInputStream,
                mManifestInputStream.available()
            )
        ).accept(mutableAxmlWriter as AxmlVisitor)
        mManifestData = mutableAxmlWriter.toByteArray()
        return this
    }

    fun onAttr(paramAttr: AxmlWriter.Attr) {
        if ("package" == paramAttr.name.data && mPackageName != null && paramAttr.value is StringItem) {
            (paramAttr.value as StringItem).data = mPackageName
            return
        }
        if (paramAttr.ns == null || "http://schemas.android.com/apk/res/android" != paramAttr.ns.data) return
        if ("versionCode" == paramAttr.name.data && mVersionCode != -1) {
            paramAttr.value = Integer.valueOf(mVersionCode)
            return
        }
        if ("versionName" == paramAttr.name.data && mVersionName != null && paramAttr.value is StringItem) {
            paramAttr.value = StringItem(mVersionName)
            (paramAttr.value as StringItem).data = mVersionName
            return
        }
        if ("label" == paramAttr.name.data && mAppName != null && paramAttr.value is StringItem) {
            (paramAttr.value as StringItem).data = mAppName
            return
        }
    }

    fun setAppName(paramString: String?): ManifestEditor {
        mAppName = paramString
        return this
    }

    fun setPackageName(paramString: String?): ManifestEditor {
        mPackageName = paramString
        return this
    }

    fun setVersionCode(paramInt: Int): ManifestEditor {
        mVersionCode = paramInt
        return this
    }

    fun setVersionName(paramString: String?): ManifestEditor {
        mVersionName = paramString
        return this
    }

    @Throws(IOException::class)
    fun writeTo(paramOutputStream: OutputStream) {
        paramOutputStream.write(mManifestData)
        paramOutputStream.close()
    }

    private inner class MutableAxmlWriter : AxmlWriter() {
        override fun child(param1String1: String, param1String2: String): NodeVisitor {
            val mutableNodeImpl = MutableNodeImpl(param1String1, param1String2)
            firsts.add(mutableNodeImpl)
            return mutableNodeImpl
        }

        private inner class MutableNodeImpl(
            param2String1: String?,
            param2String2: String?
        ) :
            NodeImpl(param2String1, param2String2) {
            override fun child(param2String1: String, param2String2: String): NodeVisitor {
                val mutableNodeImpl = MutableNodeImpl(param2String1, param2String2)
                children.add(mutableNodeImpl)
                return mutableNodeImpl
            }

            protected fun onAttr(param2Attr: Attr) {
                this@ManifestEditor.onAttr(param2Attr)
                super.onAttr(param2Attr)
            }
        }
    }

    private inner class MutableNodeImpl internal constructor(
        param1String1: String?,
        param1String2: String?
    ) :
        AxmlWriter.NodeImpl(param1String1, param1String2) {
        override fun child(param1String1: String, param1String2: String): NodeVisitor {
            val mutableNodeImpl = MutableNodeImpl(param1String1, param1String2)
            children.add(mutableNodeImpl)
            return mutableNodeImpl
        }

        protected fun onAttr(param1Attr: AxmlWriter.Attr) {
            this@ManifestEditor.onAttr(param1Attr)
            super.onAttr(param1Attr)
        }
    }

    companion object {
        private const val NS_ANDROID = "http://schemas.android.com/apk/res/android"
    }
}
