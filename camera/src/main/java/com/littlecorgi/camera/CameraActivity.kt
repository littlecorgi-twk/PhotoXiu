package com.littlecorgi.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route


@Route(path = "/camera/CameraActivity")
class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity_camera)
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.camera_container, CameraFragment.newInstance())
                .commit()
    }
}