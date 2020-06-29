package com.littlecorgi.photoxiu.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlecorgi.photoxiu.Result
import com.littlecorgi.photoxiu.bean.ongoingmovies.OngoingMovieRepository
import com.littlecorgi.photoxiu.bean.ongoingmovies.OngoingMovies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-12 14:16
 */
class MainViewModel(
        private val ongoingMovieRepository: OngoingMovieRepository
) : ViewModel() {

    private val _movies = MutableLiveData<OngoingMovies>()
    val movies: LiveData<OngoingMovies> = _movies

    private val _errorToastText = MutableLiveData<String>()
    val errorToastText: LiveData<String> = _errorToastText

    fun requestMovies() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                ongoingMovieRepository.getOngoingMovies()
            }
            if (result is Result.Success) {
                onMovieLoaded(result.data)
            } else if (result is Result.Error) {
                onDataNotAvailable(result.exception.message)
            }
        }
    }

    private fun onDataNotAvailable(message: String?) {
        _errorToastText.value = message
    }

    private fun onMovieLoaded(ongoingMovies: OngoingMovies) {
        _movies.value = ongoingMovies
    }

}