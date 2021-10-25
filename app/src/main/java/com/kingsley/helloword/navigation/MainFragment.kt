package com.kingsley.helloword.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kingsley.base.getSelfViewModel
import com.kingsley.common.L
import com.kingsley.base.fragment.BaseVmVbFragment
import com.kingsley.helloword.databinding.MainFragmentBinding
import com.tencent.mmkv.MMKV

class MainFragment : BaseVmVbFragment<MainViewModel, MainFragmentBinding>() {

    companion object {
        fun newInstance() = MainFragment()
        private const val TAG = "MainFragment"
    }

    private lateinit var viewModel: MainViewModel
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = requireActivity().getSelfViewModel {
//            loadingLiveData.observe(this@MainFragment, {
//                context?.showShort(if (it) "Is Loading" else "Is data already")
//                viewModelScope.launch {
//                    delay(1000)
//                }
//                showEmpty(true)
//            })
//            emptyLiveData.observe(this@MainFragment, {
//                context?.showShort(if (it) "Is Empty" else "I An No Empty")
//                viewModelScope.launch {
//                    delay(1000)
//                }
//                showNoNet(true)
//            })
//            noNetLiveData.observe(this@MainFragment, {
//                context?.showShort(if (it) "Is No Net" else "Net Coming soon")
//                viewModelScope.launch {
//                    delay(1000)
//                }
//                showToast("I An Toast")
//            })
//            toastLiveData.observe(this@MainFragment, {
//                context?.showShort(it)
//            })
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnWordHistory.setOnClickListener {
            val kv = MMKV.defaultMMKV()
            kv?.encode("bool", true)
            val bValue = kv?.decodeBool("bool")
            L.d("bValue = $bValue")
        }

        binding.btnInputWord.setOnClickListener {

        }

        binding.btnWordList.setOnClickListener {

        }

        binding.btnSetting.setOnClickListener {
            viewModel.onSettingClick.value = true
        }
        //val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment)
        Log.d(TAG, "onActivityCreated: $")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun layoutId(): Int {
        TODO("Not yet implemented")
    }

    override fun initView(savedInstanceState: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun initObserve() {
        TODO("Not yet implemented")
    }

    override fun lazyLoadData() {
        TODO("Not yet implemented")
    }

    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?) = MainFragmentBinding.inflate(inflater, container, false)
}