package com.kingsley.helloword.coroutine

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.kingsley.base.UiState
import com.kingsley.base.activity.BaseVmVbActivity
import com.kingsley.base.shortToast
import com.kingsley.helloword.databinding.CoroutinesActivityBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoroutinesActivity : BaseVmVbActivity<CoroutinesViewModel, CoroutinesActivityBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.showLoading()
        lifecycleScope.launch {
            delay(1000)
            mViewModel.showEmpty()
        }
        lifecycleScope.launch {
            delay(2000)
            mViewModel.showNoNet()
        }
        lifecycleScope.launch {
            delay(3000)
            mViewModel.showError()
        }
        lifecycleScope.launch {
            delay(4000)
            mViewModel.showContent("?????")
        }
    }

    override fun initObserve() {
        lifecycleScope.launch {
            mViewModel.uiState.collect {
                when (it) {
                    UiState.Loading -> {
                        mViewBinding.tvTest.text = "Loading"
                    }
                    UiState.NoNet -> {
                        mViewBinding.tvTest.text = "NoNet"
                    }
                    is UiState.Empty -> {
                        mViewBinding.tvTest.text = "Empty"
                    }
                    is UiState.Error -> {
                        mViewBinding.tvTest.text = "Error"
                    }
                    is UiState.Content -> {
                        mViewBinding.tvTest.text = it.data.toString()
                    }
                    is UiState.Toast -> {
                        shortToast(it.msg)
                    }
                }
            }
        }

    }

}