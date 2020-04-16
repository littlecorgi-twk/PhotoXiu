package com.littlecorgi.photoxiu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import com.alibaba.android.arouter.facade.annotation.Route
import com.littlecorgi.photoxiu.adapter.OngoingMovieRvAdapter
import com.littlecorgi.photoxiu.view.capturevideo.CaptureVideoActivity
import com.littlecorgi.photoxiu.viewModel.MainViewModel
import com.littlecorgi.photoxiu.databinding.AppActivityMainBinding
import com.yc.pagerlib.recycler.PagerLayoutManager

@Route(path = "/app/MainActivity")
class MainActivity : AppCompatActivity() {

    private lateinit var mViewModel: MainViewModel
    private lateinit var binding: AppActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.app_activity_main)
        mViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(MainViewModel::class.java)

        findViewById<View>(R.id.btn_capture_video).setOnClickListener {
            if (Build.VERSION.SDK_INT > 22) {
                when {
                    ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> { //先判断有没有权限 ，没有就在这里进行权限的申请
                        Log.d(TAG, "onCreate: 相机权限")
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA_OK)
                    }
                    ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED -> { //先判断有没有权限 ，没有就在这里进行权限的申请
                        Log.d(TAG, "onCreate: 麦克风权限")
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_AUDIO_OK)
                    }
                    ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> { //先判断有没有权限 ，没有就在这里进行权限的申请
                        Log.d(TAG, "onCreate: 存储权限")
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_WRITE_OK)
                    }
                    ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> { //先判断有没有权限 ，没有就在这里进行权限的申请
                        Log.d(TAG, "onCreate: 读取文件权限")
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_READ_OK)
                    }
                    else -> { //说明已经获取到摄像头权限了 想干嘛干嘛
                        startActivity(Intent(this@MainActivity, CaptureVideoActivity::class.java))
                    }
                }
            } else { //这个说明系统版本在6.0之下，不需要动态获取权限。
            }
        }
        //加载native库
//        try {
//            IjkMediaPlayer.loadLibrariesOnce(null)
//            IjkMediaPlayer.native_profileBegin("libijkplayer.so")
//        } catch (e: Exception) {
//            finish()
//        }
        requestOngoingMovies()
        initRecycler()
    }

    private fun initRecycler() {
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
                if (jzvd != null && Jzvd.CURRENT_JZVD != null &&
                        jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.currentUrl)) {
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
            Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show()
        })
    }

    private fun requestOngoingMovies() {
        mViewModel.requestMovies()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CAMERA_OK -> {
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this@MainActivity, "请手动打开相机权限", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_AUDIO_OK -> {
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this@MainActivity, "请手动打开麦克风权限", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_WRITE_OK -> {
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this@MainActivity, "请手动打开存储权限", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_READ_OK -> {
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this@MainActivity, "请手动打开读取文件权限", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val PERMISSION_CAMERA_OK = 100
        private const val PERMISSION_AUDIO_OK = 101
        private const val PERMISSION_WRITE_OK = 102
        private const val PERMISSION_READ_OK = 103
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.getKeyCode() === KeyEvent.KEYCODE_BACK) {
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
