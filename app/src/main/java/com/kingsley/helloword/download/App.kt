package com.kingsley.helloword.download
import java.io.Serializable

data class App(
    var name: String? = null,
    var icon: String? = null,
    var downloadUrl: String? = null
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}