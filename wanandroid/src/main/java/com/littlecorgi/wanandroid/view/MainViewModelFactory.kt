package com.littlecorgi.wanandroid.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.littlecorgi.wanandroid.logic.Repository
import com.littlecorgi.wanandroid.view.home.HomeViewModel

/**
 *
 * @author littlecorgi 2020/9/16
 */
class MainViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                return HomeViewModel(
                        repository = Repository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
        }
    }

}