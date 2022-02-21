package com.kingsley.tetris.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import com.kingsley.tetris.R
import com.kingsley.tetris.util.SizeUtils
import java.net.URI

class NewRecordDialog : Dialog {
    var tvScoreName: TextView? = null
    var tvScoreValue: TextView? = null
    var tvUserName: TextView? = null
    var etUserNameValue: EditText? = null
    var vDivider: View? = null
    var tvConfirm: TextView? = null

    private constructor(context: Context) : super(context) {
        init()
    }

    private constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        init()
    }

    private constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener) {
        init()
    }

    private fun init() {
        setProperty(40)
        setContentView(R.layout.dialog_new_record)
        tvScoreName = findViewById(R.id.tv_score_name)
        tvScoreValue = findViewById(R.id.tv_score_value)
        tvUserName = findViewById(R.id.tv_user_name)
        etUserNameValue = findViewById(R.id.et_user_name_value)
        vDivider = findViewById(R.id.v_divider)
        tvConfirm = findViewById(R.id.tv_confirm)
        tvConfirm?.setOnClickListener { cancel() }
    }

    class Builder {
        private var newRecordDialog: NewRecordDialog

        constructor(context: Context) {
            newRecordDialog = NewRecordDialog(context)
        }

        constructor(context: Context, themeResId: Int) {
            newRecordDialog = NewRecordDialog(context, themeResId)
        }

        constructor(
            context: Context,
            cancelable: Boolean,
            cancelListener: DialogInterface.OnCancelListener
        ) {
            newRecordDialog = NewRecordDialog(context, cancelable, cancelListener)
        }

        fun setUserNameHint(@StringRes userNameHint: Int): Builder {
            newRecordDialog.etUserNameValue!!.setHint(userNameHint)
            return this
        }

        fun setUserNameHint(userNameHint: String?): Builder {
            newRecordDialog.etUserNameValue!!.hint = userNameHint
            return this
        }

        fun setScoreValue(score: String?): Builder {
            newRecordDialog.tvScoreValue!!.text = score
            return this
        }

        fun builder(): NewRecordDialog {
            return newRecordDialog
        }
    }

    val userNameHint: String
        get() = etUserNameValue!!.hint.toString().trim { it <= ' ' }
    val userName: String
        get() = etUserNameValue!!.text.toString().trim { it <= ' ' }

    private fun setProperty(i: Int) {
        val window = window
        val p = window!!.attributes
        p.width = SizeUtils.screenWidth - SizeUtils.dp2px(i.toFloat())
        window.attributes = p
        window.setBackgroundDrawableResource(R.drawable.shape_dialog_background)
    }
}