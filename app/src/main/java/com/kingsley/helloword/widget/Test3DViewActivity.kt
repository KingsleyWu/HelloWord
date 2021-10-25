package com.kingsley.helloword.widget

import android.os.Bundle
import com.kingsley.base.activity.BaseActivity
import com.kingsley.helloword.R
import com.kingsley.helloword.ui.Test3DView

/**
 * @author Kingsley
 * Created on 2021/6/28.
 */
class Test3DViewActivity: BaseActivity() {

    private val mTest3DView: Test3DView by lazy { findViewById(R.id.test3DView) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_3d_view_activity)
    }
}