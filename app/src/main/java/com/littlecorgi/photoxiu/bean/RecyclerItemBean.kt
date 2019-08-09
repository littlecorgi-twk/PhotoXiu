package com.littlecorgi.photoxiu.bean

class RecyclerItemBean(private var name: String, private var deception: String, private val count: String) {

    fun getName(): String {
        return name
    }

    fun getDeception(): String {
        return deception
    }

    fun getCount(): String {
        return count
    }
}