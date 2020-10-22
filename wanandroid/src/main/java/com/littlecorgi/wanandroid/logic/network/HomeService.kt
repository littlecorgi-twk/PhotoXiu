package com.littlecorgi.wanandroid.logic.network

import com.littlecorgi.wanandroid.logic.model.HomeListModel
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 用于请求首页相关数据
 * https://www.wanandroid.com/blog/show/2
 * @author littlecorgi 2020/9/16
 */
interface HomeService {
    /**
     * 返回首页列表
     * @param page 页数
     */
    @GET("/article/list/{page}/json")
    suspend fun getArticleList(@Path("page") page: Int): HomeListModel
}