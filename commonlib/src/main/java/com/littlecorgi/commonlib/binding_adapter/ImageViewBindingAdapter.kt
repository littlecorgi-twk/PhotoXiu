package com.littlecorgi.commonlib.binding_adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object ImageViewBindingAdapter {
    @BindingAdapter("url")
    @JvmStatic
    fun ImageView.getImageFromUrl(url: String) {
        Glide.with(this).load(url).into(this)
    }
}