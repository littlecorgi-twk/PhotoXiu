package com.littlecorgi.androidcomponentialprogramming

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import butterknife.BindView

@Route(path = "/app/MainActivity")
class MainActivity : AppCompatActivity() {

    private lateinit var buttonChat: Button
    private lateinit var buttonFind: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonChat = findViewById(R.id.button1_activity_main)
        buttonFind = findViewById(R.id.button2_activity_main)

        ARouter.openLog()     // 打印日志
        ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init(this.application) // 尽可能早，推荐在Application中初始化

        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        buttonChat.setOnClickListener {
            ARouter.getInstance().build("/chat/ChatMainActivity")
                    .withLong("key1", 666L)
                    .withString("key2", df.format(Date()))
                    .navigation()
        }
        buttonFind.setOnClickListener {
            ARouter.getInstance().build("/find/FindActivity")
                    .withLong("key3", 666L)
                    .withString("key4", df.format(Date()))
                    .navigation()
        }
    }
}
