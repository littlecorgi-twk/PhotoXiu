package com.littlecorgi.photoxiu.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-13 18:28
 */
@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
                .load(imageUrl)
                .centerCrop()
                .into(view)
    }
}