package com.kingsley.helloword.navigation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.kingsley.base.*
import com.kingsley.helloword.R
import com.kingsley.helloword.ui.WhirlingView

class NavigationActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        if (savedInstanceState == null) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment?
            navController = navHostFragment?.navController
        }

        viewModel.onSettingClick.observe(this, {
            navController?.navigate(R.id.action_mainFragment_to_settingsFragment)
        })
    }

}