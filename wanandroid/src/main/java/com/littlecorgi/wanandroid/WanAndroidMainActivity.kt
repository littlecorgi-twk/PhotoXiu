package com.littlecorgi.wanandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.littlecorgi.wanandroid.databinding.WanandroidActivityWanAndroidMainBinding
import com.littlecorgi.wanandroid.view.home.HomeFragment

@Route(path = "/wanandroid/WanAndroidMainActivity")
class WanAndroidMainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "WanAndroidMainActivity"
        private const val FRAGMENT_HOME = "home"
    }

    private lateinit var mBinding: WanandroidActivityWanAndroidMainBinding
    private val mHomeFragment = HomeFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.wanandroid_activity_wan_android_main)

        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
                .add(mBinding.frameLayout.id, mHomeFragment, FRAGMENT_HOME)
                .commit()
    }
}