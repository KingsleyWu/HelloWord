package com.kingsley.test

import android.os.Build
import android.os.Bundle
import android.view.View
import com.kingsley.base.BaseActivity
import com.kingsley.helloword.databinding.TestActivityBinding

/**
 * @author Kingsley
 * Created on 2021/7/7.
 */
class TestActivity: BaseActivity() {

    lateinit var mTestActivityBinding: TestActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTestActivityBinding = TestActivityBinding.inflate(layoutInflater)
        setContentView(mTestActivityBinding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView: View = window.decorView
            if (decorView != null) {
                var vis = decorView.systemUiVisibility
                vis = if (false) {
                    vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                if (decorView.systemUiVisibility != vis) {
                    decorView.systemUiVisibility = vis
                }
            }
        }
    }
}