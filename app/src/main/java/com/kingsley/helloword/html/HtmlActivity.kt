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
     * @param root 最外层布局，需要调整的布局
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root，使scrollToView在root可视区域的底部
     */
    private fun controlKeyboardLayout(root: View, scrollToView: View){
        root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            //获取root在窗体的可视区域
            root.getWindowVisibleDisplayFrame(rect)
            //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
            val rootInvisibleHeight = root.rootView.height - rect.bottom
            //若不可视区域高度大于100，则键盘显示
            if (rootInvisibleHeight > 100) {
                val location = IntArray(2)
                //获取scrollToView在窗体的坐标
                scrollToView.getLocationInWindow(location)
                //计算root滚动高度，使scrollToView在可见区域
                val scrollHeight = (location[1] + scrollToView.height) - rect.bottom
                root.scrollTo(0, scrollHeight)
            } else {
                //键盘隐藏
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