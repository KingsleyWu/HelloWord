package com.kingsley.shimeji.purchases;

import android.util.Base64;

import javax.annotation.Nonnull;

public final class Encryption {
    @Nonnull
    public static String decrypt(@Nonnull String paramString1, @Nonnull String paramString2) {
        return xor(new String(Base64.decode(paramString1, Base64.DEFAULT)), paramString2);
    }

    @Nonnull
    public static String encrypt(@Nonnull String paramString1, @Nonnull String paramString2) {
        return new String(Base64.encode(xor(paramString1, paramString2).getBytes(), Base64.DEFAULT));
    }

    @Nonnull
    private static String xor(@Nonnull String paramString1, @Nonnull String paramString2) {
        char[] arrayOfChar1 = paramString1.toCharArray();
        int i = arrayOfChar1.length;
        char[] arrayOfChar2 = paramString2.toCharArray();
        int j = arrayOfChar2.length;
        char[] arrayOfChar3 = new char[i];
        for (int b = 0; b < i; b++) {
            char c = arrayOfChar1[b];
            char d = arrayOfChar2[b % j];
            arrayOfChar3[b] = (char) (c ^ d);
        }
        return new String(arrayOfChar3);
    }
}
