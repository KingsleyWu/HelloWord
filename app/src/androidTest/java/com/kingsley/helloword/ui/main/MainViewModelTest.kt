package com.kingsley.helloword.ui.main

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kingsley.helloword.navigation.MainViewModel
import org.junit.Test

import org.junit.runner.RunWith

/**
 * @author Kingsley
 * Created on 2021/5/18.
 */
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    @Test
    fun test() {
        val mainViewModel = MainViewModel()
        val test = mainViewModel.test()
        println("test = $test")
        assertThat(test != null).isTrue()
    }
}