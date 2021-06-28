package com.kingsley.helloword.threed

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kingsley.base.BaseFragment
import com.kingsley.helloword.R
import com.kingsley.helloword.ui.JigsawView

/**
 * @author Kingsley
 * Created on 2021/6/24.
 */
class JigsawFragment : BaseFragment() {
    private val mJigsawView: JigsawView by lazy { requireView().findViewById(R.id.jigsawView) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.jigsaw_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mJigsawView.setPicture(BitmapFactory.decodeResource(resources, R.drawable.test))
    }
}