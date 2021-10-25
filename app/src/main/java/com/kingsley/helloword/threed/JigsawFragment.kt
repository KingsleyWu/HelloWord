package com.kingsley.helloword.threed

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kingsley.base.fragment.BaseFragment
import com.kingsley.helloword.R
import com.kingsley.helloword.databinding.JigsawFragmentBinding

/**
 * @author Kingsley
 * Created on 2021/6/24.
 */
class JigsawFragment : BaseFragment() {
    private lateinit var mBinding : JigsawFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = JigsawFragmentBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.jigsawView.setPicture(BitmapFactory.decodeResource(resources, R.drawable.test))
    }
}