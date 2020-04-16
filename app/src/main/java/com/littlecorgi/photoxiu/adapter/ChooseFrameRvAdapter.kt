package com.littlecorgi.photoxiu.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.littlecorgi.photoxiu.R

/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-17 18:34
 */
class ChooseFrameRvAdapter(context: Context, items: ArrayList<Bitmap>) : RecyclerView.Adapter<ChooseFrameRvAdapter.ViewHolder>() {
    private var mContext: Context = context
    private var mItems: ArrayList<Bitmap> = items

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public var imageView: ImageView = itemView.findViewById(R.id.iv_frame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.app_item_rv_choose_frame, parent)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mContext).load(mItems[position]).into(holder.imageView)
    }
}