package com.kingsley.shimeji.purchases

import android.util.Base64
import com.kingsley.shimeji.purchases.Encryption
import javax.annotation.Nonnull

object Encryption {
    @Nonnull
    fun decrypt(@Nonnull paramString1: String?, @Nonnull paramString2: String): String {
        return xor(String(Base64.decode(paramString1, Base64.DEFAULT)), paramString2)
    }

    @Nonnull
    fun encrypt(@Nonnull paramString1: String, @Nonnull paramString2: String): String {
        return String(Base64.encode(xor(paramString1, paramString2).toByteArray(), Base64.DEFAULT))
    }

    @Nonnull
    private fun xor(@Nonnull paramString1: String, @Nonnull paramString2: String): String {
        val arrayOfChar1 = paramString1.toCharArray()
        val i = arrayOfChar1.size
        val arrayOfChar2 = paramString2.toCharArray()
        val j = arrayOfChar2.size
        val arrayOfChar3 = CharArray(i)
        for (b in 0 until i) {
            val c: Int = arrayOfChar1[b].code
            val d: Int  = arrayOfChar2[b % j].code
            arrayOfChar3[b] = (c xor d).toChar()
        }
        return String(arrayOfChar3)
    }
}