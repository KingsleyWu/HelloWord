package com.kingsley.helloword.systembars

import android.os.Bundle
import android.widget.Button
import com.kingsley.base.*
import com.kingsley.helloword.R

/**
 * @author Kingsley
 * Created on 2021/6/28.
 */
class SystemBarsActivity: BaseActivity() {
    private val mBtnShowStatusBar: Button by lazy { findViewById(R.id.btn_show_status_bar) }
    private val mBtnHideStatusBar: Button by lazy { findViewById(R.id.btn_hide_status_bar) }
    private val mBtnShowNavigationBar: Button by lazy { findViewById(R.id.btn_show_navigation_bar) }
    private val mBtnHideNavigationBar: Button by lazy { findViewById(R.id.btn_hide_navigation_bar) }
    private val mBtnShowSystemBars: Button by lazy { findViewById(R.id.btn_show_system_bars) }
    private val mBtnHideSystemBars: Button by lazy { findViewById(R.id.btn_hide_system_bars) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.system_bars_activity)

        mBtnShowStatusBar.setOnClickListener {
            showStatusBars()
        }
        mBtnHideStatusBar.setOnClickListener {
            hideStatusBars()
        }

        mBtnShowNavigationBar.setOnClickListener {
            showNavigationBars()
        }
        mBtnHideNavigationBar.setOnClickListener {
           hideNavigationBars()
        }

        mBtnShowSystemBars.setOnClickListener {
           showSystemBars()
        }
        mBtnHideSystemBars.setOnClickListener {
           hideSystemBars()
        }
    }
}