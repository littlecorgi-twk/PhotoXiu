package com.littlecorgi.camera

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView
import com.alibaba.android.arouter.facade.annotation.Route


@Route(path = "/camera/CameraActivity")
class CameraActivity : AppCompatActivity() {

    private val data = arrayOf("aa", "bb")//假数据

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity_camera)

        val listView = findViewById<ListView>(R.id.camera_listView)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        listView.adapter = arrayAdapter

    }
}
