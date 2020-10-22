package com.littlecorgi.wanandroid.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.littlecorgi.wanandroid.logic.Repository
import com.littlecorgi.wanandroid.logic.model.HomeListModel

class HomeViewModel(
        val repository: Repository
) : ViewModel() {

    val mDatas = ArrayList<HomeListModel.DataBean.DatasBean>()

    private val _data = MutableLiveData<Result<HomeListModel.DataBean>>()
    val date: LiveData<Result<HomeListModel.DataBean>>
        get() = _data

    private val _page = MutableLiveData<Int>()

    val getHomeList = Transformations.switchMap(_page) { page ->
        Log.d("HomeViewModel", "getHomeList: page=$page")
        repository.getHomeList(page)
        // return@switchMap loadList(page)
    }

    // private fun loadList(page: Int): LiveData<Result<HomeListModel.DataBean>>? {
    //     var result: LiveData<Result<HomeListModel.DataBean>>? = null
    //     viewModelScope.launch {
    //         val temp = try {
    //             val data = withContext(Dispatchers.IO) {
    //                 WanAndroidNetwork.getHomeList(page).data
    //             }
    //             Log.d("HomeViewModel", "loadList: $data")
    //             MutableLiveData(Result.success(data))
    //         } catch (e: Exception) {
    //             Log.d("HomeViewModel", "loadListError: $e")
    //             MutableLiveData(Result.failure(e))
    //         }
    //         val value = temp.value
    //         Log.d("HomeViewModel", "HomeViewModel.getHomeList: 1")
    //         if (value != null) {
    //             if (value.isSuccess) {
    //                 Log.d("HomeViewModel", "HomeViewModel.getHomeList: isSuccess")
    //                 _data.value = value
    //                 result = _data
    //             } else {
    //                 Log.d("HomeViewModel", "HomeViewModel.getHomeList: isFailure")
    //                 result = MutableLiveData(Result.failure(value.exceptionOrNull()!!))
    //             }
    //         } else {
    //             Log.d("HomeViewModel", "HomeViewModel.getHomeList: return is null")
    //             result = MutableLiveData(Result.failure(Exception("return is null")))
    //         }
    //     }
    //     Log.d("HomeViewModel", "HomeViewModel.getHomeList: finish")
    //     return result
    // }

    fun getHomeList(page: Int) {
        _page.value = page
    }
}