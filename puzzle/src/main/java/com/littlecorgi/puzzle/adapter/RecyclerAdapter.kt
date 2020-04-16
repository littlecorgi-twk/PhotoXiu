package com.littlecorgi.puzzle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.littlecorgi.puzzle.R
import com.littlecorgi.puzzle.bean.RecyclerItem

class RecyclerAdapter(mItemList: List<RecyclerItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    private var mOnItemClickListener: OnItemClickListener? = null

    override fun onClick(v: View?) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener!!.onItemClick(v!!, v.tag as Int)
        }
    }

    companion object {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var mItemImage: ImageView? = null
            var mItemText: TextView? = null

            init {
                mItemImage = itemView.findViewById(R.id.iv_item_recycler)
                mItemText = itemView.findViewById(R.id.tv_item_recycler)
            }
        }

        interface OnItemClickListener {
            fun onItemClick(view: View, position: Int)
        }
    }

    private var mItemList: List<RecyclerItem>? = null

    init {
        this.mItemList = mItemList
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.puzzle_item_recycler, p0, false)
        val viewHolder = ViewHolder(view)
        view.setOnClickListener(this)
        return viewHolder
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val item: RecyclerItem = mItemList!![p1]
        val viewHolder = p0 as ViewHolder
        viewHolder.itemView.tag = p1
        viewHolder.mItemText!!.text = item.getName()
        viewHolder.mItemImage!!.setImageResource(item.getImageId())
    }

    override fun getItemCount(): Int {
        return mItemList!!.size
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }
}
