package com.littlecorgi.photoxiu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class AdsActivity : AppCompatActivity() {
    private var tvTime: TextView? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_ads)

        tvTime = findViewById(R.id.app_tv_time)

        /**
         * 倒计时
         * 第一个是计时的总时长，第二个是间隔
         */
        countDownTimer = object : CountDownTimer(4000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tvTime!!.text = "跳过广告" + millisUntilFinished / 1000 + "秒"
            }

            override fun onFinish() {
                val intent = Intent(this@AdsActivity, MainOldActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()

        /**
         * 点击跳过
         */
        tvTime!!.setOnClickListener {
            countDownTimer!!.cancel()
            val intent = Intent(this@AdsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
