package com.kingsley.helloword.link

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import com.kingsley.base.activity.BaseActivity
import com.kingsley.helloword.databinding.LinkActivityBinding
import java.util.regex.Matcher
import java.util.regex.Pattern

class LinkActivity : BaseActivity() {
    lateinit var viewBinding: LinkActivityBinding
    private val WEB_URL = Pattern.compile("((http[s]?|ftp?|file?)://?[a-zA-Z0-9.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[\\u4e00-\\u9fa5a-zA-Z0-9.\\-~!@#$%^&*+?:_/=<>！￥…（）《》—？][^\\s]*)?)|(www.[a-zA-Z0-9.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9.\\-~!@#$%^&*+?:_/=<>][^\\s]*)?)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = LinkActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.tv1.setLinksText()
        viewBinding.tv2.setLinksText()
        viewBinding.tv3.setLinksText()
        viewBinding.tv4.setLinksText()
        viewBinding.tv5.setLinksText()
        viewBinding.tv6.setLinksText()
        viewBinding.tv7.setLinksText()
        viewBinding.tv8.setLinksText()
        viewBinding.tv9.setLinksText()
        viewBinding.tv10.setLinksText()
        viewBinding.tv11.setLinksText()
    }

    private fun TextView.setLinksText() {
        val spannable = SpannableStringBuilder(text)
        Linkify.addLinks(spannable, WEB_URL, null)
        val urls: Array<URLSpan> = spannable.getSpans(0, spannable.length, URLSpan::class.java)
        for (span in urls) {
            makeLinkClickable(context, spannable, span, Color.BLUE)
        }
        movementMethod = LinkMovementMethod.getInstance()
        setLinkTextColor(Color.BLUE)
        setText(spannable, TextView.BufferType.SPANNABLE)
    }

    fun makeLinkClickable(context: Context, spannable: SpannableStringBuilder, span: URLSpan, color: Int) {
        makeLinkClickable(context, spannable, span, color, false)
    }

    fun makeLinkClickable(context: Context, spannable: SpannableStringBuilder, span: URLSpan, color: Int, isUnText: Boolean) {
        val start: Int = spannable.getSpanStart(span)
        val end: Int = spannable.getSpanEnd(span)
        val flags: Int = spannable.getSpanFlags(span)
        val clickable: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Do something with span.getURL() to handle the link click...
                var uriStr = span.url
                if (uriStr.contains("mailto")) {
                    val it = Intent(Intent.ACTION_SENDTO, Uri.parse(uriStr))
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(it)
                } else {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.addCategory(Intent.CATEGORY_BROWSABLE)
                    if (isWebUrl(uriStr) && !uriStr.startsWith("http")) {
                        uriStr = "https://$uriStr"
                    }
                    intent.data = Uri.parse(uriStr)
                    context.startActivity(intent)
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = color
                ds.isUnderlineText = isUnText
            }
        }

        spannable.setSpan(clickable, start, end, flags)
        spannable.removeSpan(span)
    }

    fun isWebUrl(url: String): Boolean {
        val m: Matcher = WEB_URL.matcher(url)
        return m.find()
    }
}