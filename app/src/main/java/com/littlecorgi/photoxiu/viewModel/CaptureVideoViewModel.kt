package com.littlecorgi.photoxiu.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-16 09:49
 */
class CaptureVideoViewModel : ViewModel() {

    // Toast内容
    private val _toastContent = MutableLiveData<String>()
    val toastContent: LiveData<String> = _toastContent

    fun setToastContent(text: String) {
        _toastContent.value = text
    }
}