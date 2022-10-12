package pxb.android.axml

class Axml : AxmlVisitor() {
    var firsts: MutableList<Node?> = mutableListOf()
    var nses: MutableList<Ns?> = mutableListOf()
    fun accept(visitor: AxmlVisitor) {
        var var2: Iterator<*> = nses.iterator()
        while (var2.hasNext()) {
            val ns = var2.next() as Ns
            ns.accept(visitor)
        }
        var2 = firsts.iterator()
        while (var2.hasNext()) {
            val first = var2.next() as Node
            first.accept(visitor)
        }
    }

    override fun child(ns: String?, name: String?): NodeVisitor {
        val node = Node()
        node.name = name
        node.ns = ns
        firsts.add(node)
        return node
    }

    override fun ns(prefix: String?, uri: String?, ln: Int) {
        val ns = Ns()
        ns.prefix = prefix
        ns.uri = uri
        ns.ln = ln
        nses.add(ns)
    }

    class Ns {
        var ln = 0
        var prefix: String? = null
        var uri: String? = null
        fun accept(visitor: AxmlVisitor) {
            visitor.ns(prefix, uri, ln)
        }
    }

    class Node : NodeVisitor() {
        var attrs: MutableList<Attr?> = ArrayList()
        var children: MutableList<Node?> = ArrayList()
        var ln: Int? = null
        var ns: String? = null
        var name: String? = null
        var text: Text? = null

        fun accept(nodeVisitor: NodeVisitor?) {
            val nodeVisitor2 = nodeVisitor?.child(ns, name)
            acceptB(nodeVisitor2)
            nodeVisitor2?.end()
        }

        fun acceptB(nodeVisitor: NodeVisitor?) {
            text?.accept(nodeVisitor)
            val var2 = attrs.iterator()
            while (var2.hasNext()) {
                var2.next()?.accept(nodeVisitor)
            }
            if (ln != null) {
                nodeVisitor?.line(ln!!)
            }
            val var3 = children.iterator()
            while (var3.hasNext()) {
                var3.next()?.accept(nodeVisitor)
            }
        }

        override fun attr(ns: String?, name: String?, resourceId: Int, type: Int, obj: Any?) {
            val attr = Attr()
            attr.name = name
            attr.ns = ns
            attr.resourceId = resourceId
            attr.type = type
            attr.value = obj
            attrs.add(attr)
        }

        override fun child(ns: String?, name: String?): NodeVisitor {
            val node = Node()
            node.name = name
            node.ns = ns
            children.add(node)
            return node
        }

        override fun line(ln: Int) {
            this.ln = ln
        }

        override fun text(lineNumber: Int, value: String?) {
            val text = Text()
            text.ln = lineNumber
            text.text = value
            this.text = text
        }

        class Text {
            var ln = 0
            var text: String? = null
            fun accept(nodeVisitor: NodeVisitor?) {
                nodeVisitor!!.text(ln, text)
            }
        }

        class Attr {
            var ns: String? = null
            var name: String? = null
            var resourceId = 0
            var type = 0
            var value: Any? = null
            fun accept(nodeVisitor: NodeVisitor?) {
                nodeVisitor!!.attr(ns, name, resourceId, type, value)
            }
        }
    }
}
