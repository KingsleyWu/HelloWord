package com.kingsley.helloword.apk

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kingsley.base.BaseFragment
import com.kingsley.base.adapter.MultiTypeAdapter
import com.kingsley.helloword.R


/**
 * @author Kingsley
 * Created on 2021/6/23.
 */
class GetApkItemFragment : BaseFragment() {
    private val mRvList: RecyclerView by lazy { requireView().findViewById(R.id.rv_list) }
    private val mAdapter = MultiTypeAdapter()
    private var mType: String? = ""

    var requestPermission = registerForActivityResult(RequestMultiplePermissions()) { permissions ->
        var isGranted = false
        for (permission in permissions) {
            if (permission.value) {
                isGranted = true
            } else {
                isGranted = false
                break
            }
        }
        if (isGranted) {
            // permission granted
        } else {
            // permission denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mType = arguments?.getString(KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.single_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mType?.let {
            val installedPackages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                requireActivity().packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            } else {
                @Suppress("DEPRECATION")
                requireActivity().packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
            }
            val appList = initDataSource(it, installedPackages)
            mRvList.layoutManager = LinearLayoutManager(requireContext())
            mRvList.adapter = mAdapter.register(GetApkItemDelegate(requestPermission))
            mAdapter.items = appList.toMutableList()
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun initDataSource(type: String, appList: List<PackageInfo>): List<PackageInfo> {
        when (type) {
            USER -> {
                val userApp = mutableListOf<PackageInfo>()
                for (packageInfo in appList) {
                    if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                        userApp.add(packageInfo)
                    }
                }
                return userApp
            }
            SYSTEM -> {
                val systemApp = mutableListOf<PackageInfo>()
                for (packageInfo in appList) {
                    if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                        systemApp.add(packageInfo)
                    }
                }
                return systemApp
            }
            ALL_APP -> return appList
        }
        return appList
    }

    override fun onDestroy() {
        super.onDestroy()
        requestPermission.unregister()
    }

    companion object {
        private const val KEY = "KEY_TYPE"

        fun getInstance(type: String): GetApkItemFragment {
            return GetApkItemFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY, type)
                }
            }
        }
    }
}