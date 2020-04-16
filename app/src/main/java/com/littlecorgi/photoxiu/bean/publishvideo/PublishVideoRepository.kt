package com.littlecorgi.photoxiu.bean.publishvideo

import android.content.Context
import android.net.Uri
import com.littlecorgi.photoxiu.Result

/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-14 10:04
 */
interface PublishVideoRepository {

    fun postCoverImageAndVideo(mSelectedImage: Uri, mSelectedVideo: Uri, context: Context): Result<PostVideoResponse>
}