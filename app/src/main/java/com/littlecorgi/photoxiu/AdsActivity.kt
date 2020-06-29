package com.littlecorgi.photoxiu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.openadsdk.*
import com.littlecorgi.camera.showToast
import com.littlecorgi.photoxiu.ttadsdk.TTAdManagerHolder
import com.littlecorgi.photoxiu.utils.DisplayUtil


class AdsActivity : AppCompatActivity() {

    companion object {
        const val TAG = "AdsActivity"
        const val AD_TIME_OUT = 3000
        const val mCodeId = "887340166"
    }

    // 构建TTAdManager对象
    // TTAdManager对象为整个SDK的入口接口，可用于广告获取、权限请求、版本号获取
    private lateinit var ttAdManager: TTAdManager

    // 创建TTAdNative对象（用于调用广告请求接口）
    private lateinit var mTTAdNative: TTAdNative

    private lateinit var mSplashContainer: FrameLayout
    private var mIsExpress = false //是否请求模板广告
    private var mForceGoMain = false//是否强制跳转到主页面

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_ads)

        mSplashContainer = findViewById(R.id.splash_container)
        ttAdManager = TTAdManagerHolder.get()
        mTTAdNative = ttAdManager.createAdNative(this)
        // 在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        // 在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好
        // TTAdManagerHolder.getInstance(this).requestPermissionIfNecessary(this);
        // 加载开屏广告
        loadSplashAd()
    }

    override fun onResume() {
        if (mForceGoMain) {
            goToMainActivity();
        }
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        mForceGoMain = true
    }

    /**
     * 加载开屏广告
     */
    private fun loadSplashAd() {
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        val adSlot = if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
            //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
            val expressViewWidth: Float = DisplayUtil.sScreenWidth.toFloat()
            val expressViewHeight: Float = DisplayUtil.getScreenHeight().toFloat()
            AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(1080, 1920) //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
                    .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
                    .build()
        } else {
            AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(1080, 1920)
                    .build()
        }

        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, object : TTAdNative.SplashAdListener {
            override fun onSplashAdLoad(ad: TTSplashAd?) {
                Log.d(TAG, "开屏广告请求成功")
                if (ad == null) {
                    return
                }
                //获取SplashView
                //获取SplashView
                val view: View = ad.splashView
                if (!this@AdsActivity.isFinishing) {
                    mSplashContainer.removeAllViews()
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                    mSplashContainer.addView(view)
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
                } else {
                    goToMainActivity()
                }
                //设置SplashView的交互监听器
                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(object : TTSplashAd.AdInteractionListener {
                    override fun onAdClicked(view: View, type: Int) {
                        Log.d(TAG, "onAdClicked")
                        showToast("开屏广告点击")
                    }

                    override fun onAdShow(view: View, type: Int) {
                        Log.d(TAG, "onAdShow")
                        showToast("开屏广告展示")
                    }

                    override fun onAdSkip() {
                        Log.d(TAG, "onAdSkip")
                        showToast("开屏广告跳过")
                        goToMainActivity()
                    }

                    override fun onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver")
                        showToast("开屏广告倒计时结束")
                        goToMainActivity()
                    }
                })
                if (ad.interactionType == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(object : TTAppDownloadListener {
                        var hasShow = false
                        override fun onIdle() {
                        }

                        override fun onDownloadPaused(p0: Long, p1: Long, p2: String?, p3: String?) {
                            showToast("下载暂停...")
                        }

                        override fun onDownloadFailed(p0: Long, p1: Long, p2: String?, p3: String?) {
                            showToast("下载失败...")
                        }

                        override fun onDownloadActive(p0: Long, p1: Long, p2: String?, p3: String?) {
                            if (!hasShow) {
                                showToast("下载中...")
                                hasShow = true
                            }
                        }

                        override fun onDownloadFinished(p0: Long, p1: String?, p2: String?) {
                            showToast("下载完成...")
                        }

                        override fun onInstalled(p0: String?, p1: String?) {
                            showToast("安装完成...")
                        }
                    })
                }
            }

            override fun onTimeout() {
                showToast("开屏广告加载超时")
                goToMainActivity()
            }

            override fun onError(p0: Int, p1: String?) {
                Log.d(TAG, "$p1")
                showToast("$p1")
                goToMainActivity()
            }
        }, AD_TIME_OUT)
    }

    /**
     * 跳转到主页面
     */
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        mSplashContainer.removeAllViews()
        finish()
    }
}
