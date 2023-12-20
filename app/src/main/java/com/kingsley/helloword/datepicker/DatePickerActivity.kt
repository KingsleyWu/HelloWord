package com.kingsley.helloword.datepicker

import android.os.Bundle
import com.kingsley.base.activity.BaseActivity
import com.kingsley.helloword.databinding.DatePickerActivityBinding
import com.kingsley.helloword.databinding.DrawActivityBinding

class DatePickerActivity : BaseActivity() {

    private lateinit var mBinding: DatePickerActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DatePickerActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.dialogSubmit.setOnClickListener {

        }
    }
}