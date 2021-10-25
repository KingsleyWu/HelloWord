package com.kingsley.helloword.coroutine

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.kingsley.base.UiState
import com.kingsley.base.activity.BaseVmVbActivity
import com.kingsley.helloword.R
import com.kingsley.helloword.databinding.CoroutinesActivityBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CoroutinesActivity : BaseVmVbActivity<CoroutinesViewModel, CoroutinesActivityBinding>() {

    override fun initViewBinding(inflater: LayoutInflater) = CoroutinesActivityBinding.inflate(inflater)

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
                        mViewBind.tvTest.text = "Loading"
                    }
                    UiState.NoNet -> {
                        mViewBind.tvTest.text = "NoNet"
                    }
                    is UiState.Empty -> {
                        mViewBind.tvTest.text = "Empty"
                    }
                    is UiState.Error -> {
                        mViewBind.tvTest.text = "Error"
                    }
                    is UiState.ShowContent<*> -> {
                        mViewBind.tvTest.text = it.data.toString()
                    }
                }
            }
        }

    }

}