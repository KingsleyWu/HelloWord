package com.kingsley.helloword.tts

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.kingsley.common.L
import java.util.*
import kotlin.math.min

class TextToSpeechManager(context: Context) {

    companion object{
        const val ANDROID_VERSION_NOT_SUPPORT = -100
    }

    private var ttsInitStatus : Int = ANDROID_VERSION_NOT_SUPPORT

    private var tts: TextToSpeech = TextToSpeech(context) {
        ttsInitStatus = it
        L.d("init ttsInitStatus = $ttsInitStatus")
    }

    fun setLanguages(locale: Locale) : Int{
        ttsInitStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.setLanguage(locale)
        } else {
            ANDROID_VERSION_NOT_SUPPORT
        }
        L.d("setLanguages ttsInitStatus = $ttsInitStatus")
        return ttsInitStatus
    }

    /**
     * 读文本
     * @param content 文本内容
     * @param utteranceProgressListener 阅读进度监听
     */
    fun speak(
        content: String,
        utteranceProgressListener: UtteranceProgressListener
    ) {
        L.d("ttsInitStatus = $ttsInitStatus")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { return }
        tts.setOnUtteranceProgressListener(utteranceProgressListener)
        when (ttsInitStatus) {
            TextToSpeech.LANG_MISSING_DATA -> L.d("LANG_MISSING_DATA")
            TextToSpeech.LANG_NOT_SUPPORTED -> L.d("LANG_NOT_SUPPORTED")
            ANDROID_VERSION_NOT_SUPPORT -> L.d("ANDROID_VERSION_NOT_SUPPORT")
            else -> {
                if (tts.isSpeaking) {
                    tts.stop()
                } else {
                    tts.speak(content, TextToSpeech.QUEUE_FLUSH, null, null)
//                    val textSeg = genSegment(content, 10)
//                    for (i in textSeg.indices) {
//                        // QUEUE_ADD：播放完之前的语音任务后才播报本次内容，QUEUE_FLUSH：丢弃之前的播报任务，立即播报本次内容
//                        tts.speak(textSeg[i], TextToSpeech.QUEUE_ADD, null, SEG_PREFIX + i)
//                    }
                }
            }
        }
    }

    /**
     * 将源文本分段
     *
     * @param segmentLength 每一段的长度，最大设置 3999，大于 3999 将阅读出错
     * @param originStr     源文本
     * @return 分成的文本段
     */
    private fun genSegment(originStr: String, segmentLength: Int = 3999): Array<String?> {
        val originLength = originStr.length
        val arraySize = originLength / segmentLength + 1
        val result = arrayOfNulls<String>(arraySize)
        for (i in 0 until arraySize) {
            result[i] = originStr.substring(i * segmentLength, min((i + 1) * segmentLength, originLength))
        }
        return result
    }
}