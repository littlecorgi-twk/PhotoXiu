package com.littlecorgi.photoxiu

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.littlecorgi.commonlib.utils.DisplayUtil
import com.littlecorgi.photoxiu.ttadsdk.TTAdManagerHolder


class MyMainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdManagerHolder.init(this)

        // 初始化 ARouter
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        ARouter.openLog()     // 打印日志
        ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init(this)

        // 初始化DisplayUtil
        DisplayUtil.sContext = this
        DisplayUtil.sScreenWidth = DisplayUtil.getScreenWidth()
        DisplayUtil.sScreenHeight = DisplayUtil.getScreenHeight()
    }
}