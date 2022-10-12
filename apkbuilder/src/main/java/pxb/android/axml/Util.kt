package pxb.android.axml

import java.io.*

object Util {
    @Throws(IOException::class)
    fun readFile(file: File): ByteArray {
        val inputStream: InputStream = FileInputStream(file)
        val xml = ByteArray(inputStream.available())
        inputStream.read(xml)
        inputStream.close()
        return xml
    }

    @Throws(IOException::class)
    fun readIs(inputStream: InputStream): ByteArray {
        val os = ByteArrayOutputStream()
        copy(inputStream, os)
        return os.toByteArray()
    }

    @Throws(IOException::class)
    fun writeFile(data: ByteArray, out: File) {
        val fos = FileOutputStream(out)
        fos.write(data)
        fos.close()
    }

    @Throws(IOException::class)
    fun readProguardConfig(config: File): Map<String, String> {
        val clzMap: MutableMap<String, String> = HashMap()
        val r = BufferedReader(InputStreamReader(FileInputStream(config), "utf8"))
        r.use { buff ->
            var ln = buff.readLine()
            while (ln != null) {
                if (!ln.startsWith("#") && !ln.startsWith(" ")) {
                    val i = ln.indexOf("->")
                    if (i > 0) {
                        clzMap[ln.substring(0, i).trim { it <= ' ' }] =
                            ln.substring(i + 2, ln.length - 1).trim { it <= ' ' }
                    }
                }
                ln = buff.readLine()
            }
        }
        return clzMap
    }

    @Throws(IOException::class)
    fun copy(inputStream: InputStream, os: OutputStream) {
        val xml = ByteArray(10240)
        var c = inputStream.read(xml)
        while (c > 0) {
            os.write(xml, 0, c)
            c = inputStream.read(xml)
        }
    }
}
