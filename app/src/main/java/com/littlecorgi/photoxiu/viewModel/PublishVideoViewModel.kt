package com.littlecorgi.photoxiu.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlecorgi.photoxiu.Result
import com.littlecorgi.photoxiu.bean.publishvideo.PostVideoResponse
import com.littlecorgi.photoxiu.bean.publishvideo.PublishVideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-14 10:00
 */
class PublishVideoViewModel(
        private val publishVideoRepository: PublishVideoRepository
) : ViewModel() {

    private val _errorToastText = MutableLiveData<String>()
    val errorToastText: LiveData<String> = _errorToastText

    private val _postResponse = MutableLiveData<PostVideoResponse>()
    val postResponse: LiveData<PostVideoResponse> = _postResponse

    fun postVideo(mSelectedImage: Uri, mSelectedVideo: Uri, context: Context) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                publishVideoRepository.postCoverImageAndVideo(mSelectedImage, mSelectedVideo, context)
            }
            if (result is Result.Success) {
                onPostSucceed(result.data)
            } else if (result is Result.Error) {
                onDataNotAvailable(result.exception.message)
            }
        }
    }

    private fun onDataNotAvailable(message: String?) {
        _errorToastText.value = message
        Log.d("MainActivity", "onDataNotAvailable(message): ${_errorToastText.value}")
    }

    private fun onPostSucceed(postVideoResponse: PostVideoResponse) {
        _postResponse.value = postVideoResponse
    }

}