package com.kingsley.helloword.widget

import android.graphics.BitmapFactory
import android.os.Bundle
import com.kingsley.base.activity.BaseActivity
import com.kingsley.helloword.R
import com.kingsley.helloword.ui.JigsawView

/**
 * @author Kingsley
 * Created on 2021/6/28.
 */
class JigsawViewActivity : BaseActivity() {

    private val mJigsawView: JigsawView by lazy { findViewById(R.id.jigsawView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jigsaw_view_activity)
        mJigsawView.setPicture(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.test
            )
        )
    }
}