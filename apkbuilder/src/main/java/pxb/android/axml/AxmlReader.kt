package pxb.android.axml

import java.io.IOException
import java.util.*

class AxmlReader(data: ByteArray) {
    val parser: AxmlParser = AxmlParser(data)

    @Throws(IOException::class)
    fun accept(av: AxmlVisitor) {
        val nvs: Stack<NodeVisitor?> = Stack()
        var tos: NodeVisitor? = av
        label34@ while (true) {
            when (val type: Int = parser.next()) {
                1, 5 -> {}
                2 -> {
                    nvs.push(tos)
                    tos = tos?.child(parser.namespaceUri, parser.name)
                    if (tos != null) {
                        if (tos === EMPTY_VISITOR) {
                            break
                        }
                        tos.line(parser.lineNumber)
                        var i = 0
                        while (true) {
                            if (i >= parser.attrCount) {
                                continue@label34
                            }
                            tos.attr(
                                parser.getAttrNs(i),
                                parser.getAttrName(i),
                                parser.getAttrResId(i),
                                parser.getAttrType(i),
                                parser.getAttrValue(i)
                            )
                            ++i
                        }
                    }
                    tos = EMPTY_VISITOR
                }
                3 -> {
                    tos?.end()
                    tos = nvs.pop()
                }
                4 -> av.ns(
                    parser.namespacePrefix,
                    parser.namespaceUri,
                    parser.lineNumber
                )
                6 -> tos?.text(parser.lineNumber, parser.text)
                7 -> return
                else -> System.err.println("AxmlReader: Unsupported tag: $type")
            }
        }
    }

    companion object {
        val EMPTY_VISITOR: NodeVisitor = object : NodeVisitor() {

            override fun child(ns: String?, name: String?): NodeVisitor {
                return this
            }
        }
    }
}
