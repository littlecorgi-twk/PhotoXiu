package com.littlecorgi.puzzle.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.littlecorgi.puzzle.BaseActivity
import com.littlecorgi.puzzle.R
import com.littlecorgi.puzzle.adapter.RecyclerAdapter
import com.littlecorgi.puzzle.bean.RecyclerItem
import com.yalantis.ucrop.UCrop
import java.io.File

@Route(path = "/puzzle/PuzzleActivity")
class PuzzleActivity : BaseActivity() {

    companion object {
        // 相册请求码
        private const val ALBUM_REQUEST_CODE: Int = 1
        // Edit请求码
        private const val EDITACTIVITY_REQUEST_CODE: Int = 2
    }

    private lateinit var mToolbar: Toolbar
    private lateinit var mImageView: ImageView
    private lateinit var mRecycler: RecyclerView

    private var uri: Uri? = null
//    // 根据uri得到图片之后，先转为bitmap进行后续操作，并删除原文件
//    private var bitmap: Bitmap? = null

    private var mItemList = ArrayList<RecyclerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_activity_puzzle)

        //建议在application 的onCreate()的方法中调用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }

        getPicFromAlbum()

        // 透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.statusBarColor = Color.TRANSPARENT //防止5.x以后半透明影响效果，使用这种透明方式
        }

        initView()
        initToolbar()
        initRecyclerView()
    }

    /**
     * 从相册获取图片
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun getPicFromAlbum() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION)
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val mimeTypes = arrayOf("image/jpeg", "image/png")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }

            startActivityForResult(Intent.createChooser(intent, getString(R.string.puzzle_label_select_picture)), ALBUM_REQUEST_CODE)
        }
    }

    /**
     * 裁剪图片
     */
    private fun cropPhoto(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "temp" + System.currentTimeMillis() + ".jpeg"))
        val options = UCrop.Options()
        options.setShowCropGrid(false)
        UCrop.of(uri, destinationUri)
                .withOptions(options)
                .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ALBUM_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data!!.data
                    mImageView.setImageURI(uri)
                }
            }
            EDITACTIVITY_REQUEST_CODE -> {
                val bundle = data!!.extras
                uri = bundle!!.getParcelable("uri")
                mImageView.setImageURI(uri)
//                // uri转为bitmap
//                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//                // 删除uri原文件
//                contentResolver.delete(uri!!, null, null)
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    uri = UCrop.getOutput(data!!)
                    mImageView.setImageURI(uri)
                }
            }
            UCrop.RESULT_ERROR -> {
                val cropError = UCrop.getError(data!!)
                cropError!!.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar_activity_puzzle)
        mImageView = findViewById(R.id.imageView_activity_puzzle)
        mRecycler = findViewById(R.id.recycler_activity_puzzle)
    }

    private fun initToolbar() {
        setSupportActionBar(mToolbar)
        mToolbar.setNavigationOnClickListener { finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_toolbart_activity_puzzle, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.finish -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        initRecyclerItem()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mRecycler.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapter(mItemList)
        adapter.setOnItemClickListener(object : RecyclerAdapter.Companion.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        val intent = Intent()
                        intent.setClass(this@PuzzleActivity, EditActivity::class.java)
                        intent.putExtra("Uri", uri)
                        startActivityForResult(intent, EDITACTIVITY_REQUEST_CODE)
                    }
                    1 -> cropPhoto(uri!!)
                    else -> Toast.makeText(baseContext, "1234", Toast.LENGTH_SHORT).show()
                }
            }
        })
        mRecycler.adapter = adapter
    }

    private fun initRecyclerItem() {
        val edit = RecyclerItem("增强", R.drawable.ic_tune_black_24dp)
        mItemList.add(edit)
        val clip = RecyclerItem("裁剪", R.drawable.ic_crop_black_24dp)
        mItemList.add(clip)
        val filter = RecyclerItem("滤镜", R.drawable.ic_photo_filter_black_24dp)
        mItemList.add(filter)
        val frames = RecyclerItem("相框", R.drawable.ic_filter_frames_black_24dp)
        mItemList.add(frames)
    }
}
