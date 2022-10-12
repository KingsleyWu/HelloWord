package pxb.android.axml

class ValueWrapper private constructor(val type: Int, val ref: Int, val raw: String) {

    fun replaceRaw(raw: String): ValueWrapper {
        return ValueWrapper(type, ref, raw)
    }

    companion object {
        const val ID = 1
        const val STYLE = 2
        const val CLASS = 3
        fun wrapId(ref: Int, raw: String): ValueWrapper {
            return ValueWrapper(1, ref, raw)
        }

        fun wrapStyle(ref: Int, raw: String): ValueWrapper {
            return ValueWrapper(2, ref, raw)
        }

        fun wrapClass(ref: Int, raw: String): ValueWrapper {
            return ValueWrapper(3, ref, raw)
        }
    }
}
