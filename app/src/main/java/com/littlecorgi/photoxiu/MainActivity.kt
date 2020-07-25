package com.littlecorgi.photoxiu

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import com.alibaba.android.arouter.facade.annotation.Route
import com.littlecorgi.commonlib.BaseActivity
import com.littlecorgi.commonlib.utils.startActivity
import com.littlecorgi.photoxiu.adapter.OngoingMovieRvAdapter
import com.littlecorgi.photoxiu.databinding.AppActivityMainBinding
import com.littlecorgi.photoxiu.view.capturevideo.CaptureVideoActivity
import com.littlecorgi.photoxiu.viewModel.MainViewModel
import com.yc.pagerlib.recycler.PagerLayoutManager

@Route(path = "/app/MainActivity")
class MainActivity : BaseActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val PERMISSION_CAMERA_OK = 100
        private const val PERMISSION_AUDIO_OK = 101
        private const val PERMISSION_WRITE_OK = 102
        private const val PERMISSION_READ_OK = 103
        private const val ALL_PERMISSION_OK = 104
    }

    private lateinit var mViewModel: MainViewModel
    private lateinit var binding: AppActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.app_activity_main)
        mViewModel = ViewModelProvider(this, ViewModelFactory()).get(MainViewModel::class.java)

        findViewById<View>(R.id.btn_capture_video).setOnClickListener {
            if (Build.VERSION.SDK_INT > 22) {
                val permissionsList = mutableListOf<String>()
                if (ContextCompat.checkSelfPermission(this@MainActivity,
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) { //先判断有没有权限 ，没有就在这里进行权限的申请
                    Log.d(TAG, "onCreate: 相机权限")
                    permissionsList.add(Manifest.permission.CAMERA)
                }
                if (ContextCompat.checkSelfPermission(this@MainActivity,
                                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) { //先判断有没有权限 ，没有就在这里进行权限的申请
                    Log.d(TAG, "onCreate: 麦克风权限")
                    permissionsList.add(Manifest.permission.RECORD_AUDIO)
                }
                if (ContextCompat.checkSelfPermission(this@MainActivity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //先判断有没有权限 ，没有就在这里进行权限的申请
                    Log.d(TAG, "onCreate: 存储权限")
                    permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                if (ContextCompat.checkSelfPermission(this@MainActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //先判断有没有权限 ，没有就在这里进行权限的申请
                    Log.d(TAG, "onCreate: 读取文件权限")
                    permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                if (permissionsList.isNotEmpty()) {
                    ActivityCompat.requestPermissions(this, permissionsList.toTypedArray(), 104)
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_LONG).show()
                    startActivity<CaptureVideoActivity> {
                        null
                    }
                }
            } else { //这个说明系统版本在6.0之下，不需要动态获取权限。
                Toast.makeText(this, "权限获取成功,版本低于Android6.0", Toast.LENGTH_LONG).show()
                startActivity<CaptureVideoActivity> {
                    null
                }
            }
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
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "相机权限获取成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "请手动打开相机权限", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_AUDIO_OK -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "麦克风权限获取成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "请手动打开麦克风权限", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_WRITE_OK -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "存储权限获取成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "请手动打开存储权限", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_READ_OK -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "文件权限获取成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "请手动打开读取文件权限", Toast.LENGTH_SHORT).show()
                }
            }
            ALL_PERMISSION_OK -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity<CaptureVideoActivity> {
                        null
                    }
                } else {
                    Toast.makeText(this@MainActivity, "请手动打开相机、麦克风、存储、文件读取权限", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            }
        }
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
