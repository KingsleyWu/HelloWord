package com.kingsley.helloword.html

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.kingsley.base.activity.BaseActivity
import com.kingsley.base.dp
import com.kingsley.helloword.R

class HtmlActivity : BaseActivity() {
    companion object{
        lateinit var attributes: WindowManager.LayoutParams
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
        }
        val view = View(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 20.dp)
            setBackgroundColor(Color.BLUE)
        }
        layout.addView(view)
        val webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.RED)
        }
        layout.addView(webView)

        setContentView(layout)
        webView.loadUrl("file:///android_asset/test.html")
        view.setOnClickListener {
            click()
        }
    }

    private fun click() {
        HtmlDialogFragment().show(supportFragmentManager, "tag")
    }
}

class HtmlDialogFragment : DialogFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
//        dialog?.window?.attributes = HtmlActivity.attributes
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = LinearLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
        }
        val editText = EditText(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40.dp)
            setBackgroundColor(Color.BLUE)
            imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN
        }
        layout.addView(editText)
        val webView = WebView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.GREEN)
        }
        webView.loadUrl("file:///android_asset/test.html")
        layout.addView(webView)
        return layout
    }

    /**
     * @param root ???????????????????????????????????????
     * @param scrollToView ??????????????????scrollToView?????????root??????scrollToView???root?????????????????????
     */
    private fun controlKeyboardLayout(root: View, scrollToView: View){
        root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            //??????root????????????????????????
            root.getWindowVisibleDisplayFrame(rect)
            //??????root?????????????????????????????????(?????????View?????????????????????)
            val rootInvisibleHeight = root.rootView.height - rect.bottom
            //??????????????????????????????100??????????????????
            if (rootInvisibleHeight > 100) {
                val location = IntArray(2)
                //??????scrollToView??????????????????
                scrollToView.getLocationInWindow(location)
                //??????root??????????????????scrollToView???????????????
                val scrollHeight = (location[1] + scrollToView.height) - rect.bottom
                root.scrollTo(0, scrollHeight)
            } else {
                //????????????
                root.scrollTo(0, 0)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            val width = 300.dp
            val height = 300.dp
            it.setLayout(width, height)
            it.setBackgroundDrawable(ColorDrawable())
        }
    }
}