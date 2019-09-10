package com.littlecorgi.retouch

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/retouch/RetouchActivity")
class RetouchActivity : AppCompatActivity() {

    private var glSurfaceView: GLSurfaceView? = null
    private var rendererSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.retouch_activity_retouch)
        glSurfaceView = GLSurfaceView(this)

        // 检查系统是否支持OpenGL ES 2.0
        val activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        // 同时判断系统是不是模拟器
        val supportsEs2: Boolean =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")))

        // 配置渲染表面
        if (supportsEs2) {
            glSurfaceView!!.setEGLContextClientVersion(2)

            glSurfaceView!!.setRenderer(FirstOpenGLProjectRenderer(this))
            rendererSet = true
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_LONG).show()
        }

        setContentView(glSurfaceView)
    }

    override fun onPause() {
        super.onPause()

        if (rendererSet) {
            glSurfaceView!!.onPause()
        }
    }

    override fun onResume() {
        super.onResume()

        if (rendererSet) {
            glSurfaceView!!.onResume()
        }
    }
}
