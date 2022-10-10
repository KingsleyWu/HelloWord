package com.kingsley.helloword.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.kingsley.base.UiState
import com.kingsley.common.L
import com.kingsley.base.fragment.BaseVmVbFragment
import com.kingsley.base.shortToast
import com.kingsley.helloword.databinding.MainFragmentBinding
import com.tencent.mmkv.MMKV

class MainFragment : BaseVmVbFragment<MainViewModel, MainFragmentBinding>() {

    companion object {
        fun newInstance() = MainFragment()
        private const val TAG = "MainFragment"
    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewBinding.run {
            btnWordHistory.setOnClickListener {
                val kv = MMKV.defaultMMKV()
                kv?.encode("bool", true)
                val bValue = kv?.decodeBool("bool")
                L.d("bValue = $bValue")
            }

            btnInputWord.setOnClickListener {

            }

            btnWordList.setOnClickListener {

            }

            btnSetting.setOnClickListener {
                mViewModel.onSettingClick.value = true
            }
        }

    }

    override fun initObserve() {
        lifecycleScope.launchWhenStarted {
            mViewModel.uiState.collect {
                when (it) {
                    UiState.Loading -> {
                        context?.shortToast("Loading")
                    }
                    UiState.NoNet -> {
                        context?.shortToast("NoNet")
                    }
                    is UiState.Empty -> {
                        context?.shortToast("Empty")
                    }
                    is UiState.Error -> {
                        context?.shortToast("Error")
                    }
                    is UiState.Content -> {
                        context?.shortToast(it.data.toString())
                    }
                    is UiState.Toast -> {
                        context?.shortToast(it.msg)
                    }
                }
            }
        }
    }

    override fun lazyLoadData() {

    }

    override fun viewBinding(inflater: LayoutInflater, container: ViewGroup?) = MainFragmentBinding.inflate(inflater, container, false)
}