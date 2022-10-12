package pxb.android.axml


abstract class NodeVisitor {
    var nv: NodeVisitor? = null

    constructor()

    constructor(nv: NodeVisitor?) {
        this.nv = nv
    }

    open fun attr(ns: String?, name: String?, resourceId: Int, type: Int, obj: Any?) {
        nv?.attr(ns, name, resourceId, type, obj)
    }

    open fun child(ns: String?, name: String?): NodeVisitor? {
        return nv?.child(ns, name)
    }

    open fun end() {
        nv?.end()
    }

    open fun line(ln: Int) {
        nv?.line(ln)
    }

    open fun text(lineNumber: Int, value: String?) {
        nv?.text(lineNumber, value)
    }

    companion object {
        const val TYPE_FIRST_INT = 16
        const val TYPE_INT_BOOLEAN = 18
        const val TYPE_INT_HEX = 17
        const val TYPE_REFERENCE = 1
        const val TYPE_STRING = 3
    }
}
