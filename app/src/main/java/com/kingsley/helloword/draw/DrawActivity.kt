package com.kingsley.helloword.draw

import android.os.Bundle
import com.kingsley.base.activity.BaseActivity
import com.kingsley.helloword.databinding.DrawActivityBinding

class DrawActivity : BaseActivity() {

    private lateinit var mBinding: DrawActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DrawActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}