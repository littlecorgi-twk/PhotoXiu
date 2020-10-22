package com.littlecorgi.wanandroid.logic.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit构造器
 * @author littlecorgi 2020/9/16
 */
object WanAndroidServiceCreator {

    private const val BASE_URL = "https://www.wanandroid.com"

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder()
                    // .addInterceptor(HttpLoggingInterceptor {
                    //     Log.i("WanAndroidNetwork", "retrofitBack: $it")
                    // }.apply {
                    //     level = HttpLoggingInterceptor.Level.BODY
                    // })
                    .build())
            .build()

    // 通过传入的泛型去构建对应的接口的实例
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    // 简化create方法
    inline fun <reified T> create(): T = create(T::class.java)
}