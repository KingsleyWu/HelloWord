package com.kingsley.helloword

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.kingsley.base.activity.BaseActivity
import com.kingsley.helloword.databinding.CrashActivityBinding
import java.lang.RuntimeException
import java.util.ArrayList

class CrashActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mBinding: CrashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = CrashActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.tv1.setOnClickListener(this)
        mBinding.tv2.setOnClickListener(this)
        mBinding.tv3.setOnClickListener(this)
        mBinding.tv4.setOnClickListener(this)
        mBinding.tv5.setOnClickListener(this)
        mBinding.tv6.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_1 -> {
                "12.3".toInt()
            }
            R.id.tv_2 -> {
                val list = ArrayList<String>()
                list[5]
            }
            R.id.tv_3 -> {
                val activity: Activity? = null
                activity!!.finish()
            }
            R.id.tv_4 -> {
                Thread {
                    Toast.makeText(this, "吐司", Toast.LENGTH_SHORT).show()
                }.start()
            }
            R.id.tv_5 -> {
                Handler(Looper.getMainLooper()).post { throw RuntimeException("handler异常") }
            }
            R.id.tv_6 -> {
                val list = ArrayList<Int>()
                for (i in 0..99) {
                    list.add(i)
                }
                val iterator: Iterator<Int> = list.iterator()
                while (iterator.hasNext()) {
                    val integer = iterator.next()
                    if (integer % 2 == 0) {
                        list.remove(integer)
                    }
                }
            }
        }
    }
}