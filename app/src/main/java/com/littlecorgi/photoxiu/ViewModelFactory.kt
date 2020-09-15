package com.littlecorgi.photoxiu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.littlecorgi.photoxiu.bean.ongoingmovies.DefaultOngoingMovieRepository
import com.littlecorgi.photoxiu.bean.publishvideo.DefaultPublishVideoRepository
import com.littlecorgi.photoxiu.feed.FeedViewModel
import com.littlecorgi.photoxiu.viewModel.CaptureVideoViewModel
import com.littlecorgi.photoxiu.viewModel.ChooseFrameViewModel
import com.littlecorgi.photoxiu.viewModel.MainViewModel
import com.littlecorgi.photoxiu.viewModel.PublishVideoViewModel

/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-13 15:48
 */
class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                return MainViewModel(
                        ongoingMovieRepository = DefaultOngoingMovieRepository()
                ) as T
            }
            modelClass.isAssignableFrom(PublishVideoViewModel::class.java) -> {
                return PublishVideoViewModel(
                        publishVideoRepository = DefaultPublishVideoRepository()
                ) as T
            }
            modelClass.isAssignableFrom(CaptureVideoViewModel::class.java) -> {
                return CaptureVideoViewModel(
                ) as T
            }
            modelClass.isAssignableFrom(ChooseFrameViewModel::class.java) -> {
                return ChooseFrameViewModel(
                ) as T
            }
            modelClass.isAssignableFrom(FeedViewModel::class.java) -> {
                return FeedViewModel(
                        ongoingMovieRepository = DefaultOngoingMovieRepository()
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}