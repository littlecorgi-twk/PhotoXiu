package com.littlecorgi.wanandroid.logic.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * WanAndroid网络请求器
 * @author littlecorgi 2020/9/16
 */
object WanAndroidNetwork {

    private val homeService = WanAndroidServiceCreator.create<HomeService>()

    suspend fun getHomeList(page: Int) = homeService.getArticleList(page)

    /**
     * 用协程封装请求返回数据，来自《第一行代码》(第三版)
     * 但是由于Retrofit本身支持协程，所以取消这个方法
     */
    @Deprecated(message = "Retrofit支持直接使用协程")
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            Log.d("WanAndroidNetwork", "await: 开始网络请求")
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    Log.d("WanAndroidNetwork", "onResponse: $body")
                    body?.let {
                        continuation.resume(body)
                    } ?: continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.d("WanAndroidNetwork", "onFailure: 1")
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}