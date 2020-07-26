package com.littlecorgi.commonlib.binding_adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView


object RecyclerViewBindingAdapter {

    @BindingAdapter("adapter", "list", requireAll = false)
    @JvmStatic
    fun RecyclerView.bindRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
        this.run {
            this.setHasFixedSize(true)
            this.adapter = adapter
        }
    }
}