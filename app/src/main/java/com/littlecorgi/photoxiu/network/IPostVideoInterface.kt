package com.littlecorgi.photoxiu.network

import com.littlecorgi.photoxiu.bean.publishvideo.PostVideoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-09 22:26
 */
interface IPostVideoInterface {
    @Multipart
    @POST("user/upload.do")
    fun uploadOneFile(@Part part: MultipartBody.Part?): Call<PostVideoResponse?>?
}