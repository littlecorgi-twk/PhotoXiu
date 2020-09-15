package com.littlecorgi.photoxiu

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import com.alibaba.android.arouter.facade.annotation.Route
import com.githang.statusbar.StatusBarCompat
import com.littlecorgi.commonlib.BaseActivity
import com.littlecorgi.commonlib.utils.startActivity
import com.littlecorgi.photoxiu.adapter.OngoingMovieRvAdapter
import com.littlecorgi.photoxiu.databinding.AppActivityMainBinding
import com.littlecorgi.photoxiu.view.capturevideo.CaptureVideoActivity
import com.littlecorgi.photoxiu.viewModel.MainViewModel
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import com.yc.pagerlib.recycler.PagerLayoutManager

@Route(path = "/app/MainActivity")
class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mViewModel: MainViewModel
    private lateinit var binding: AppActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.app_activity_main)
        mViewModel = ViewModelProvider(this, ViewModelFactory()).get(MainViewModel::class.java)

        // 设置状态栏为黑色
        StatusBarCompat.setStatusBarColor(this, Color.BLACK)

        findViewById<View>(R.id.btn_capture_video).setOnClickListener {
            val deniedPermissions = ArrayList<String>()
            deniedPermissions.add(Permission.CAMERA)
            deniedPermissions.add(Permission.RECORD_AUDIO)
            deniedPermissions.add(Permission.WRITE_EXTERNAL_STORAGE)
            deniedPermissions.add(Permission.READ_EXTERNAL_STORAGE)
            AndPermission.with(this)
                    .runtime()
                    .permission(
                            Permission.CAMERA,
                            Permission.RECORD_AUDIO,
                            Permission.WRITE_EXTERNAL_STORAGE,
                            Permission.READ_EXTERNAL_STORAGE
                    )
                    .onGranted {
                        makeShortToast("权限全部获取成功")
                        startActivity<CaptureVideoActivity> {
                            null
                        }
                    }
                    .onDenied {
                        makeLongToast("请手动开启这些权限，否则应用无法正常使用：" +
                                "${Permission.transformText(this, deniedPermissions)}")
                    }
                    .start()
        }
        requestOngoingMovies()
        initRecycler()
    }

    private fun initRecycler() {
        Log.d(TAG, "initRecycler: ${Thread.currentThread().name}")
        binding.rvFeed.layoutManager = PagerLayoutManager(
                this, OrientationHelper.VERTICAL
        )
//        val adapter = RecyclerAdapter(this)
        val adapter = OngoingMovieRvAdapter(this, ArrayList())
        binding.rvFeed.adapter = adapter
        binding.rvFeed.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                val jzvd: Jzvd = view.findViewById(R.id.ijkPlayer)
                // 当当前的item被滑出时释放资源
                if (Jzvd.CURRENT_JZVD != null && jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.currentUrl)) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos()
                    }
                }
            }

            override fun onChildViewAttachedToWindow(view: View) {
                val jzvd: Jzvd = view.findViewById(R.id.ijkPlayer)
                // 当滑动到当前item时自动播放
                jzvd.postDelayed({ jzvd.startVideo() }, 500)
            }
        })
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: OngoingMovieRvAdapter) {
        mViewModel.movies.observe(this, androidx.lifecycle.Observer { movies ->
            adapter.items.clear()
            adapter.items.addAll(movies.ms!!)
            adapter.notifyDataSetChanged()
        })
        mViewModel.errorToastText.observe(this, androidx.lifecycle.Observer { errorText ->
            makeShortToast(errorText)
        })
    }

    private fun requestOngoingMovies() {
        mViewModel.requestMovies()
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
