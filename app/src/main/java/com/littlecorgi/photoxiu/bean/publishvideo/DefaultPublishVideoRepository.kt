package com.littlecorgi.photoxiu.bean.publishvideo

import android.content.Context
import android.net.Uri
import com.littlecorgi.photoxiu.Result
import com.littlecorgi.photoxiu.network.IPostVideoInterface
import com.littlecorgi.photoxiu.utils.ResourceUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.ConnectException

/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-14 10:07
 */
class DefaultPublishVideoRepository : PublishVideoRepository {
    override fun postCoverImageAndVideo(mSelectedImage: Uri, mSelectedVideo: Uri, context: Context): Result<PostVideoResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://www.zhangshuo.fun/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val postVideoInterface = retrofit.create(IPostVideoInterface::class.java)
        //        MultipartBody.Part video = getMultipartFromUri("video", mSelectedVideo);
        //        MultipartBody.Part image = getMultipartFromUri("cover_image", mSelectedImage);
        //        List<MultipartBody.Part> parts = new ArrayList<>();
        //        parts.add(video);
        //        parts.add(image);
        val image = getMultipartFromUri("image", mSelectedImage, context)
        val uploadCall = postVideoInterface.uploadOneFile(image)
        return try {
            val response = uploadCall!!.execute()
            Result.Success(response.body()!!)
        } catch (e: ConnectException) {
            Result.Error(e)
        } catch (e: IOException) {
            Result.Error(e)
        }
    }

    private fun getMultipartFromUri(name: String, uri: Uri?, context: Context): MultipartBody.Part { // if NullPointerException thrown, try to allow storage permission in system settings
        val f = File(ResourceUtils.getRealPath(context, uri!!))
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f)
        return MultipartBody.Part.createFormData(name, f.name, requestFile)
    }
}