package com.kingsley.tetris

import android.os.Bundle
import android.view.Window
import android.widget.GridView

/**
 * @author Kingsley
 * Created on 2021/7/20.
 */
class TestActivity : BaseActivity() {

    private val mGvBlockBoard: GridView by lazy { findViewById(R.id.gv_block_board) }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

}