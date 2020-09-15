package com.littlecorgi.photoxiu

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cn.jzvd.Jzvd
import com.alibaba.android.arouter.facade.annotation.Route
import com.githang.statusbar.StatusBarCompat
import com.littlecorgi.commonlib.BaseActivity
import com.littlecorgi.commonlib.utils.startActivity
import com.littlecorgi.photoxiu.databinding.AppActivityMainBinding
import com.littlecorgi.photoxiu.feed.FeedFragment
import com.littlecorgi.photoxiu.view.capturevideo.CaptureVideoActivity
import com.littlecorgi.photoxiu.viewModel.MainViewModel
import com.yanzhenjie.permission.runtime.Permission

@Route(path = "/app/MainActivity")
class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mViewModel: MainViewModel
    private lateinit var mBinding: AppActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.app_activity_main)
        mViewModel = ViewModelProvider(this, ViewModelFactory()).get(MainViewModel::class.java)

        // 设置状态栏为黑色
        StatusBarCompat.setStatusBarColor(this, Color.BLACK)
        initClick()
        initFragment()
    }

    private fun initFragment() {
        val feedFragment = FeedFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .add(mBinding.fragment.id, feedFragment, "Feeds")
                .commit()
    }

    private fun initClick() {
        mBinding.btnCaptureVideo.setOnClickListener {
            requestCapturePermission(this, arrayOf(
                    Permission.CAMERA,
                    Permission.RECORD_AUDIO,
                    Permission.WRITE_EXTERNAL_STORAGE,
                    Permission.READ_EXTERNAL_STORAGE)) {
                makeShortToast("权限全部获取成功")
                startActivity<CaptureVideoActivity> {
                    null
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            return if (isTaskRoot) {
                moveTaskToBack(false)
                true
            } else {
                super.onKeyDown(keyCode, event)
            }
        }
        return false
    }
}
