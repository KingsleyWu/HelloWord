package com.kingsley.helloword.widget

import android.os.Bundle
import com.kingsley.base.activity.BaseActivity
import com.kingsley.helloword.R
import com.kingsley.helloword.ui.WhirlingView

/**
 * @author Kingsley
 * Created on 2021/6/28.
 */
class WhirlingViewActivity: BaseActivity() {

    private val mWhirlingView: WhirlingView by lazy { findViewById(R.id.whirlingView) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.whirling_view_activity)
        mWhirlingView.setText("我是誰")
    }
}