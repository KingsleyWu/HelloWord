package com.kingsley.helloword.keyboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.kingsley.base.activity.BaseActivity
import com.kingsley.base.hideSoftInput
import com.kingsley.base.showKeyboard
import com.kingsley.helloword.R


/**
 * @author Kingsley
 * Created on 2021/6/28.
 */
class KeyboardActivity : BaseActivity() {
    private val mTvKeyboardHeight: TextView by lazy { findViewById(R.id.tv_keyboard_height) }
    private val mAttachmentsRecyclerView: RecyclerView by lazy { findViewById(R.id.attachments_recycler_view) }
    private val mEdtKeyboard: AppCompatEditText by lazy { findViewById(R.id.edt_keyboard) }
    private val mBtnShowKeyboard: Button by lazy { findViewById(R.id.btn_show_keyboard) }
    private val mBtnHideKeyboard: Button by lazy { findViewById(R.id.btn_hide_keyboard) }
    private var keyboardShowing = false
    private lateinit var mAttachmentsRepo : AttachmentsRepo
    private lateinit var mAttachmentsRecyclerViewAdapter : AttachmentsRecyclerViewAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keyboard_activity)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            mTvKeyboardHeight.text = "鍵盤是否顯示：$imeVisible, 鍵盤高度為：$imeHeight"
            keyboardShowing = imeVisible
            insets
        }
        mAttachmentsRepo = AttachmentsRepo(this)
        mAttachmentsRecyclerView.setHasFixedSize(true)
//        mAttachmentsRepo.getAllUris()
        mAttachmentsRecyclerViewAdapter = AttachmentsRecyclerViewAdapter(mutableListOf())
        mAttachmentsRecyclerView.adapter = mAttachmentsRecyclerViewAdapter
        val receiver = ImageInputReceiver(mAttachmentsRepo, mAttachmentsRecyclerViewAdapter)
        ViewCompat.setOnReceiveContentListener(mEdtKeyboard, ImageInputReceiver.SUPPORTED_MIME_TYPES, receiver)

        mBtnShowKeyboard.setOnClickListener {
            if (!keyboardShowing) {
                showKeyboard()
            }
            keyboardShowing = !keyboardShowing
        }

        mBtnHideKeyboard.setOnClickListener {
            if (keyboardShowing) {
                hideSoftInput()
            }
            keyboardShowing = !keyboardShowing
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}