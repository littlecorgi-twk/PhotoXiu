package com.littlecorgi.puzzle.bean

class RecyclerItem(private var name: String, private var imageId: Int) {

    fun getName(): String {
        return name
    }

    fun getImageId(): Int {
        return imageId
    }
}