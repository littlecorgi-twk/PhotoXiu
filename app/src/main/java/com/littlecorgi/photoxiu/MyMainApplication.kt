package com.littlecorgi.photoxiu

import android.app.Application
import com.littlecorgi.photoxiu.ttadsdk.TTAdManagerHolder


class MyMainApplication : Application() {

    companion object {
        const val PROCESS_NAME_XXXX = "process_name_xxxx"
    }

    override fun onCreate() {
        super.onCreate()
        // 穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdManagerHolder.init(this)
    }
}