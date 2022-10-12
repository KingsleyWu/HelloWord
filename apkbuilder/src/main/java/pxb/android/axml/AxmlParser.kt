package pxb.android.axml

import pxb.android.ResConst
import pxb.android.StringItems
import pxb.android.axml.ValueWrapper.Companion.wrapId
import pxb.android.axml.ValueWrapper.Companion.wrapStyle
import pxb.android.axml.ValueWrapper.Companion.wrapClass
import java.io.IOException
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import kotlin.Throws
import kotlin.experimental.and

class AxmlParser(buffer: ByteBuffer) : ResConst {
    var attrCount = 0
        private set
    private lateinit var attrs: IntBuffer
    private var classAttribute = 0
    private var fileSize: Int = -1
    private var idAttribute = 0
    private val inBuffer: ByteBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN)
    var lineNumber = 0
        private set
    private var nameIdx = 0
    private var nsIdx = 0
    private var prefixIdx = 0
    private var resourceIds: IntArray? = null
    private var strings: Array<String?> = arrayOf()
    private var styleAttribute = 0
    private var textIdx = 0

    constructor(data: ByteArray) : this(ByteBuffer.wrap(data))

    fun getAttrName(i: Int): String? {
        val idx = attrs[i * 5 + 1]
        return strings[idx]
    }

    fun getAttrNs(i: Int): String? {
        val idx = attrs[i * 5 + 0]
        return if (idx >= 0) strings[idx] else null
    }

    fun getAttrRawString(i: Int): String? {
        val idx = attrs[i * 5 + 2]
        return if (idx >= 0) strings[idx] else null
    }

    fun getAttrResId(i: Int): Int {
        if (resourceIds != null) {
            val idx = attrs[i * 5 + 1]
            if (idx >= 0 && idx < resourceIds!!.size) {
                return resourceIds!![idx]
            }
        }
        return -1
    }

    fun getAttrType(i: Int): Int {
        return attrs[i * 5 + 3] shr 24
    }

    fun getAttrValue(i: Int): Any? {
        val v = attrs[i * 5 + 4]
        return when (i) {
            idAttribute -> wrapId(v, getAttrRawString(i)!!)
            styleAttribute -> wrapStyle(v, getAttrRawString(i)!!)
            classAttribute -> wrapClass(v, getAttrRawString(i)!!)
            else -> when (getAttrType(i)) {
                3 -> strings[v]
                18 -> v != 0
                else -> v
            }
        }
    }

    val name: String?
        get() = strings[nameIdx]
    val namespacePrefix: String?
        get() = strings[prefixIdx]
    val namespaceUri: String?
        get() = if (nsIdx >= 0) strings[nsIdx] else null
    val text: String?
        get() = strings[textIdx]

    @Throws(IOException::class)
    operator fun next(): Int {
        return if (fileSize < 0) {
            val type = inBuffer.int and '\uffff'.code
            if (type != 3) {
                throw RuntimeException()
            } else {
                fileSize = inBuffer.int
                1
            }
        } else {
            var p = inBuffer.position()
            while (p < fileSize) {
                var size: Int
                val event: Int
                label47@ while (true) {
                    val type = inBuffer.int and '\uffff'.code
                    size = inBuffer.int
                    val count: Int
                    when (type) {
                        START_FILE -> {
                            strings = StringItems.read(inBuffer)
                            inBuffer.position(p + size)
                        }
                        256 -> {
                            lineNumber = inBuffer.int
                            prefixIdx = inBuffer.int
                            nsIdx = inBuffer.int
                            event = START_NS
                            break@label47
                        }
                        257 -> {
                            inBuffer.position(p + size)
                            event = END_NS
                            break@label47
                        }
                        258 -> {
                            lineNumber = inBuffer.int
                            nsIdx = inBuffer.int
                            nameIdx = inBuffer.int
                            count = inBuffer.int
                            if (count != 1310740) {
                                throw RuntimeException()
                            }
                            attrCount = (inBuffer.short and '\uffff'.code.toShort()).toInt()
                            idAttribute = (inBuffer.short and '\uffff'.code.toShort()) - 1
                            classAttribute = idAttribute
                            styleAttribute = idAttribute
                            attrs = inBuffer.asIntBuffer()
                            event = START_TAG
                            break@label47
                        }
                        259 -> {
                            inBuffer.position(p + size)
                            event = END_TAG
                            break@label47
                        }
                        260 -> {
                            lineNumber = inBuffer.int
                            textIdx = inBuffer.int
                            event = TEXT
                            break@label47
                        }
                        384 -> {
                            count = size / 4 - 2
                            resourceIds = IntArray(count)
                            var i = 0
                            while (i < count) {
                                resourceIds!![i] = inBuffer.int
                                ++i
                            }
                            inBuffer.position(p + size)
                        }
                        else -> throw RuntimeException()
                    }
                    p = inBuffer.position()
                    continue
                }
                inBuffer.position(p + size)
                return event
            }
            END_FILE
        }
    }

    companion object {
        const val START_FILE = 1
        const val START_TAG = 2
        const val END_TAG = 3
        const val START_NS = 4
        const val END_NS = 5
        const val TEXT = 6
        const val END_FILE = 7
    }
}