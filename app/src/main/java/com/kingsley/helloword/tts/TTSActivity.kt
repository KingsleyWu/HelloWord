package com.kingsley.helloword.tts

import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import androidx.core.widget.addTextChangedListener
import com.kingsley.base.activity.BaseActivity
import com.kingsley.common.L
import com.kingsley.helloword.databinding.TtsActivityBinding
import java.util.Locale

class TTSActivity : BaseActivity() {
    private lateinit var mBinding : TtsActivityBinding
    private lateinit var mTextToSpeechManager: TextToSpeechManager
    private var mUtteranceProgressListener = object : UtteranceProgressListener(){
        override fun onStart(utteranceId: String?) {
            L.d("TTSActivity, onStart utteranceId = $utteranceId")
        }

        override fun onDone(utteranceId: String?) {
            L.d("TTSActivity, onDone utteranceId = $utteranceId")
        }

        @Deprecated("Deprecated in Java")
        @Suppress("DeprecatedCallableAddReplaceWith")
        override fun onError(utteranceId: String) {
            L.d("TTSActivity, onError utteranceId = $utteranceId")
        }

        override fun onError(utteranceId: String?, errorCode: Int) {
            L.d("TTSActivity, onError utteranceId = $utteranceId, errorCode = $errorCode")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = TtsActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mTextToSpeechManager= TextToSpeechManager(this)

        mBinding.editTts.addTextChangedListener {
            mBinding.tvTtsContent.text = it
        }

        mBinding.btnUkTts.setOnClickListener {
            // 英式英语
            mTextToSpeechManager.setLanguages(Locale.UK)
            mTextToSpeechManager.speak(mBinding.tvTtsContent.text.toString(), mUtteranceProgressListener)
        }

        mBinding.btnUsTts.setOnClickListener {
            //美式英语
            mTextToSpeechManager.setLanguages(Locale.UK)
            mTextToSpeechManager.speak(mBinding.tvTtsContent.text.toString(), mUtteranceProgressListener)
        }
    }
}