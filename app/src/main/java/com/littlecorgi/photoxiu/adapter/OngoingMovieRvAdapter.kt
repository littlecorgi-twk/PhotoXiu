package com.littlecorgi.photoxiu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.JZDataSource
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.littlecorgi.photoxiu.R
import com.littlecorgi.photoxiu.bean.ongoingmovies.OngoingMovies


/**
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-14 17:31
 */
class OngoingMovieRvAdapter(private val context: Context, var items: ArrayList<OngoingMovies.MsBean>) : RecyclerView.Adapter<OngoingMovieRvAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.iv_feed)
        var tvAuthor: TextView = itemView.findViewById(R.id.tv_author)
        var tvContent: TextView = itemView.findViewById(R.id.tv_context)
        var ijkPlayer: JzvdStd = itemView.findViewById(R.id.ijkPlayer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.app_item_rv_feed, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAuthor.text = items[position].actors
        val content = "#${items[position].tCn}"
        holder.tvContent.text = content

        val jzDataSource = JZDataSource(
                "http://lf1-hscdn-tos.pstatp.com/obj/developer-baas/baas/tt7217xbo2wz3cem41/a8efa55c5c22de69_1560563154288.mp4"
        )
        jzDataSource.looping = true
        holder.ijkPlayer.setUp(jzDataSource, Jzvd.SCREEN_NORMAL)
        Glide.with(context).load(items[position].img).centerCrop().into(holder.ijkPlayer.posterImageView)   //推荐使用Glide
        holder.imageView.visibility = View.GONE
    }
}