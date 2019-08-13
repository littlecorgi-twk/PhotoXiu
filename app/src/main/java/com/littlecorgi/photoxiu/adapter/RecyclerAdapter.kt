package com.littlecorgi.photoxiu.adapter

import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.littlecorgi.photoxiu.R
import com.littlecorgi.photoxiu.bean.RecyclerItemBean

class RecyclerAdapter(mItemList: List<RecyclerItemBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var mDeception: TextView? = null
            var mName: TextView? = null
            var mCount: TextView? = null
            var mImage: ImageView? = null
            var mCardView: CardView? = null

            init {
                mDeception = itemView.findViewById(R.id.tv_description)
                mName = itemView.findViewById(R.id.tv_user)
                mCount = itemView.findViewById(R.id.tv_love_count)
                mImage = itemView.findViewById(R.id.iv_pic)
                mCardView = itemView.findViewById(R.id.item_card)
            }
        }
    }

    private var mItemList: List<RecyclerItemBean>? = null

    init {
        this.mItemList = mItemList
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.app_item_recycler, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val item: RecyclerItemBean = mItemList!![p1]
        val viewHolder = p0 as ViewHolder
        viewHolder.mName!!.text = item.getName()
        viewHolder.mCount!!.text = item.getCount()
        viewHolder.mDeception!!.text = item.getDeception()
        if (p1 % 2 == 0) {
            viewHolder.mImage!!.setImageResource(R.drawable.pic1)
            viewHolder.mCardView!!.setBackgroundColor(Color.argb(30, 240, 229, 222))
        } else {
            viewHolder.mImage!!.setImageResource(R.drawable.pic2)
            viewHolder.mCardView!!.setBackgroundColor(Color.argb(30, 171, 208, 206))
        }
    }

    override fun getItemCount(): Int {
        return mItemList!!.size
    }
}