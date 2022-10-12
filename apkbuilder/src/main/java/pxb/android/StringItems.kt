package pxb.android

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class StringItems : ArrayList<StringItem>() {
    companion object {
        private const val UTF8_FLAG = 256

        @Throws(IOException::class)
        fun read(buffer: ByteBuffer): Array<String?> {
            val trunkOffset = buffer.position() - 8
            val stringCount = buffer.int
            val flags = buffer.int
            val stringDataOffset = buffer.int
            val offsets = IntArray(stringCount)
            val strings = arrayOfNulls<String>(stringCount)
            var base = 0
            while (base < stringCount) {
                offsets[base] = buffer.int
                ++base
            }
            base = trunkOffset + stringDataOffset
            for (i in offsets.indices) {
                buffer.position(base + offsets[i])
                var s: String
                var u8len: Int
                if (0 == flags and UTF8_FLAG) {
                    u8len = u16length(buffer)
                    s = String(buffer.array(), buffer.position(), u8len * 2, StandardCharsets.UTF_16LE)
                } else {
                    u8len = u8length(buffer)
                    val start = buffer.position()
                    var blength = u8len
                    while (buffer[start + blength].toInt() != 0) {
                        ++blength
                    }
                    s = String(buffer.array(), start, blength, StandardCharsets.UTF_8)
                }
                strings[i] = s
            }
            return strings
        }

        fun u16length(buffer: ByteBuffer): Int {
            var length: Int = buffer.short.toInt() and '\uffff'.code.toShort().toInt()
            if (length > 32767) {
                // length = (length & 32767) << 8 | in.getShort() & '\uffff';
                length = length and 32767 shl 8 or buffer.short.toInt() and '\uffff'.code.toShort().toInt()
            }
            return length
        }

        fun u8length(buffer: ByteBuffer): Int {
            var len: Int = buffer.get().toInt() and 255
            if (len and 128 != 0) {
                //  len = (len & 127) << 8 | in.get() & 255;
                len = len and 127 shl 8 or buffer.get().toInt() and 255
            }
            return len
        }

    }

    var stringData: ByteArray = ByteArray(0)
    private var useUTF8 = true

    fun getAllSize(): Int {
        return 20 + size * 4 + stringData.size + 0
    }

    @Throws(IOException::class)
    fun prepare() {
        val var1: Iterator<*> = this.iterator()
        while (var1.hasNext()) {
            val (data) = var1.next() as StringItem
            if (data!!.length > 32767) {
                useUTF8 = false
            }
        }
        val baos = ByteArrayOutputStream()
        var i = 0
        var offset = 0
        baos.reset()
        val map: MutableMap<String?, Int?> = HashMap()
        val var5: Iterator<*> = this.iterator()
        while (var5.hasNext()) {
            val item = var5.next() as StringItem
            item.index = i++
            val stringData = item.data
            val of = map[stringData]
            if (of != null) {
                item.dataOffset = of
            } else {
                item.dataOffset = offset
                map[stringData] = offset
                var length: Int
                var data: ByteArray
                var u8lenght: Int
                if (useUTF8) {
                    length = stringData!!.length
                    data = stringData.toByteArray(charset("UTF-8"))
                    u8lenght = data.size
                    if (length > 127) {
                        ++offset
                        baos.write(length shr 8 or 128)
                    }
                    baos.write(length)
                    if (u8lenght > 127) {
                        ++offset
                        baos.write(u8lenght shr 8 or 128)
                    }
                    baos.write(u8lenght)
                    baos.write(data)
                    baos.write(0)
                    offset += 3 + u8lenght
                } else {
                    length = stringData!!.length
                    data = stringData.toByteArray(charset("UTF-16LE"))
                    if (length > 32767) {
                        u8lenght = length shr 16 or '耀'.code
                        baos.write(u8lenght)
                        baos.write(u8lenght shr 8)
                        offset += 2
                    }
                    baos.write(length)
                    baos.write(length shr 8)
                    baos.write(data)
                    baos.write(0)
                    baos.write(0)
                    offset += 4 + data.size
                }
            }
        }
        stringData = baos.toByteArray()
    }

    @Throws(IOException::class)
    fun write(out: ByteBuffer) {
        out.putInt(size)
        out.putInt(0)
        out.putInt(if (useUTF8) UTF8_FLAG else 0)
        out.putInt(28 + size * 4)
        out.putInt(0)
        val var2: Iterator<*> = this.iterator()
        while (var2.hasNext()) {
            val (_, dataOffset) = var2.next() as StringItem
            out.putInt(dataOffset)
        }
        out.put(stringData)
    }
}