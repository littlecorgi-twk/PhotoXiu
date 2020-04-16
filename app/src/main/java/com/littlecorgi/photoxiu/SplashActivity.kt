package com.littlecorgi.photoxiu

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, AdsActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}
