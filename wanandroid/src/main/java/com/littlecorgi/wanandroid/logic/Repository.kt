package com.littlecorgi.wanandroid.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.littlecorgi.wanandroid.logic.network.WanAndroidNetwork
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author littlecorgi 2020/9/16
 */
object Repository {

    /**
     * 获取首页文章列表
     */
    fun getHomeList(page: Int) = fire(Dispatchers.IO) {
        val homeList = WanAndroidNetwork.getHomeList(page)
        if (homeList.errorCode == 0) {
            val list = homeList.data
            Result.success(list)
        } else {
            Result.failure(RuntimeException("response status is ${homeList.errorMsg}"))
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
            liveData(context) {
                Log.d("WanAndroidNetwork", "fire: start")
                val result = try {
                    Log.d("WanAndroidNetwork", "fire: try")
                    block()
                } catch (e: Exception) {
                    Log.d("WanAndroidNetwork", "fire: catch")
                    Result.failure(e)
                }
                Log.d("WanAndroidNetwork", "fire: end")
                emit(result)
            }
}