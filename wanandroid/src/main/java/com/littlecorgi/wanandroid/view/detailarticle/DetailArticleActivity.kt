package com.littlecorgi.wanandroid.view.detailarticle

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.widget.Toolbar
import com.littlecorgi.commonlib.BaseActivity
import com.littlecorgi.wanandroid.R

class DetailArticleActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wanandroid_activity_detail_article)

        val url = intent.getStringExtra("article_url")
        val title = intent.getStringExtra("article_title")
        url?.let {
            findViewById<WebView>(R.id.webview).loadUrl(it)
        } ?: showErrorToast(this, "URL错误：为null")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        title?.let {
            toolbar.title = it
        } ?: showErrorToast(this, "title错误：为null")
    }
}