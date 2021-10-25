package com.kingsley.download.bean

import androidx.room.TypeConverter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class Converters {

    @TypeConverter
    fun toByteArray(serializable: Serializable?): ByteArray? {
        serializable ?: return null
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        var objectOutputStream: ObjectOutputStream? = null
        try {
            byteArrayOutputStream = ByteArrayOutputStream()
            objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(serializable)

            objectOutputStream.flush()
            return byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            byteArrayOutputStream?.close()
            objectOutputStream?.close()
        }
        return null
    }

    @TypeConverter
    fun toSerializable(byteArray: ByteArray?): Serializable? {
        byteArray ?: return null
        var byteArrayOutputStream: ByteArrayInputStream? = null
        var objectInputStream: ObjectInputStream? = null
        try {
            byteArrayOutputStream = ByteArrayInputStream(byteArray)
            objectInputStream = ObjectInputStream(byteArrayOutputStream)
            return objectInputStream.readObject() as Serializable
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            byteArrayOutputStream?.close()
            objectInputStream?.close()
        }
        return null
    }
}