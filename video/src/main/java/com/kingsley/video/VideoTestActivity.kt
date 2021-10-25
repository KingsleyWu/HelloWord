package com.kingsley.video

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingsley.base.activity.BaseActivity
import com.kingsley.video.databinding.VideoTestActivityBinding

class VideoTestActivity : BaseActivity() {
    private lateinit var mBinding: VideoTestActivityBinding
    private val mLinearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = VideoTestActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.rvVideoLayout.apply {
            layoutManager = mLinearLayoutManager
        }
    }
}