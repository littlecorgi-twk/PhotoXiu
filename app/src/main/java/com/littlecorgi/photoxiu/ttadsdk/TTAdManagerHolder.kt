package com.littlecorgi.photoxiu.ttadsdk

import android.content.Context
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdSdk

object TTAdManagerHolder {
    private var sInit = false

    fun get(): TTAdManager {
        if (!sInit) {
            throw RuntimeException("TTAdSdk is not init, please check.")
        }
        return TTAdSdk.getAdManager()
    }

    fun init(context: Context) {
        doInit(context)
    }

    private fun doInit(context: Context) {
        if (!sInit) {
            TTAdSdk.init(context, buildConfig())
            sInit = true
        }
    }

    private fun buildConfig(): TTAdConfig {
        return TTAdConfig.Builder()
                .appId("5082618")
                //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .useTextureView(false)
                .appName("PhotoXiu")
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                //允许直接下载的网络状态集合
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_4G)
                .supportMultiProcess(false) //是否支持多进程，true支持
                //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okHttp3版本的样例，其余请自行开发或者咨询工作人员。
                .build()
    }
}