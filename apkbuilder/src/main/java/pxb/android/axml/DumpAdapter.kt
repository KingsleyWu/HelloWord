package pxb.android.axml

open class DumpAdapter @JvmOverloads constructor(
    nv: NodeVisitor? = null,
    var deep: Int = 0,
    var nses: MutableMap<String?, String?>? = HashMap()
) : AxmlVisitor(nv) {

    override fun attr(ns: String?, name: String?, resourceId: Int, type: Int, obj: Any?) {
        for (i in 0 until deep) {
            print("  ")
        }
        if (ns != null) {
            print(String.format("%s:", getPrefix(ns)))
        }
        print(name)
        if (resourceId != -1) {
            print(String.format("(%08x)", resourceId))
        }
        if (obj is String) {
            print(String.format("=[%08x]\"%s\"", type, obj))
        } else if (obj is Boolean) {
            print(String.format("=[%08x]\"%b\"", type, obj))
        } else if (obj is ValueWrapper) {
            val w = obj
            print(String.format("=[%08x]@%08x, raw: \"%s\"", type, w.ref, w.raw))
        } else if (type == 1) {
            print(String.format("=[%08x]@%08x", type, obj))
        } else {
            print(String.format("=[%08x]%08x", type, obj))
        }
        println()
        super.attr(ns, name, resourceId, type, obj)
    }

    override fun child(ns: String?, name: String?): NodeVisitor? {
        for (i in 0 until deep) {
            print("  ")
        }
        print("<")
        if (ns != null) {
            print(getPrefix(ns) + ":")
        }
        println(name)
        val nv: NodeVisitor? = super.child(ns, name)
        return if (nv != null) DumpAdapter(nv, deep + 1, nses) else null
    }

    fun getPrefix(uri: String): String {
        if (nses != null) {
            val prefix = nses!![uri]
            if (prefix != null) {
                return prefix
            }
        }
        return uri
    }

    override fun ns(prefix: String?, uri: String?, ln: Int) {
        println("$prefix=$uri")
        nses!![uri] = prefix
        super.ns(prefix, uri, ln)
    }

    override fun text(lineNumber: Int, value: String?) {
        for (i in 0 until deep + 1) {
            print("  ")
        }
        print("T: ")
        println(value)
        super.text(lineNumber, value)
    }
}
