package com.littlecorgi.puzzle.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.littlecorgi.puzzle.R
import com.littlecorgi.puzzle.bean.RecyclerItem

class RecyclerAdapter(mItemList: List<RecyclerItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var mItemImage: ImageView? = null
            var mItemText: TextView? = null

            init {
                mItemImage = itemView.findViewById(R.id.iv_item_recycler)
                mItemText = itemView.findViewById(R.id.tv_item_recycler)
            }
        }
    }

    private var mItemList: List<RecyclerItem>? = null

    init {
        this.mItemList = mItemList
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.puzzle_item_recycler, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val item: RecyclerItem = mItemList!![p1]
        val viewHolder = p0 as ViewHolder
        viewHolder.mItemText!!.text = item.getName()
        viewHolder.mItemImage!!.setImageResource(item.getImageId())
    }

    override fun getItemCount(): Int {
        return mItemList!!.size
    }
}