package com.littlecorgi.wanandroid.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.littlecorgi.commonlib.utils.DateUtil
import com.littlecorgi.wanandroid.R
import com.littlecorgi.wanandroid.logic.model.HomeListModel

/**
 *
 * @author littlecorgi 2020/9/16
 */
class HomeArticlesRecyclerViewAdapter(val list: List<HomeListModel.DataBean.DatasBean>) : RecyclerView.Adapter<HomeArticlesRecyclerViewAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_home_list_title)
        val fresh: TextView = view.findViewById(R.id.tv_home_list_fresh)
        val author: TextView = view.findViewById(R.id.tv_home_list_author)
        val date: TextView = view.findViewById(R.id.tv_home_list_date)
        val type: TextView = view.findViewById(R.id.tv_home_list_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wanandroid_item_home_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(list[position]) {
            holder.title.text = title
            if (!fresh) {
                holder.fresh.text = "官方"
            }
            holder.author.text = author
            val date = DateUtil.timeStamp2Date(shareDate.toString(), "")
            holder.date.text = date
            holder.type.text = when (type) {
                0 -> "官方"
                1 -> "分享"
                else -> "其它"
            }
        }
    }

    override fun getItemCount(): Int = list.size
}