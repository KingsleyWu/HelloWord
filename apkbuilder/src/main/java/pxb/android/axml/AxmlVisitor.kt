package pxb.android.axml

open class AxmlVisitor : NodeVisitor {
    constructor()
    constructor(av: NodeVisitor?) : super(av)

    open fun ns(prefix: String?, uri: String?, ln: Int) {
        (nv as AxmlVisitor?)?.ns(prefix, uri, ln)
    }
}
