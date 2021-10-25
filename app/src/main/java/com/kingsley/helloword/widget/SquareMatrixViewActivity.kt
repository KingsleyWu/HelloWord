package com.kingsley.helloword.widget

import android.graphics.Color
import android.os.Bundle
import com.kingsley.base.activity.BaseActivity
import com.kingsley.helloword.ui.DashboardView

/**
 * @author Kingsley
 * Created on 2021/7/19.
 */
class SquareMatrixViewActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DashboardView(this).apply { setBackgroundColor(Color.WHITE) })
    }
}