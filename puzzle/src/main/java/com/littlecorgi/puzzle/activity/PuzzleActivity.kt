package com.littlecorgi.puzzle.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.littlecorgi.commonlib.BaseActivity
import com.littlecorgi.puzzle.R
import com.littlecorgi.puzzle.adapter.RecyclerAdapter
import com.littlecorgi.puzzle.bean.RecyclerItem
import com.littlecorgi.puzzle.util.ProcedureUtil
import com.qiniu.android.common.FixedZone
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UpProgressHandler
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.storage.UploadOptions
import com.yalantis.ucrop.UCrop
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Route(path = "/puzzle/PuzzleActivity")
class PuzzleActivity : BaseActivity() {

    companion object {
        private const val TAG = "PuzzleActivity"

        // 相册请求码
        private const val ALBUM_REQUEST_CODE: Int = 1

        // Edit请求码
        private const val EDIT_ACTIVITY_REQUEST_CODE: Int = 2

        // Filter请求码
        private const val Filter_ACTIVITY_REQUEST_CODE: Int = 3
        private const val ACCESS_KEY = "o9oOUcAzuwZwrwBqWIVlNmI6ayD9H9x-jtHuz5HY"
        private const val SECRET_KEY = "hnEeEunY8sl9K1k74ZBsAMEIF3O-HJa9r6-am8NU"
        private const val BUCKET = "blog-markdown"
        private const val TOKEN = "o9oOUcAzuwZwrwBqWIVlNmI6ayD9H9x-jtHuz5HY:xaMz7K1gx9P_1jVNOoA32-Hzu38=:eyJzY29wZSI6ImJsb2ctbWFya2Rvd24iLCJkZWFkbGluZSI6MTU3MjMzODUwNH0="
    }

    private lateinit var mToolbar: Toolbar
    private lateinit var mImageView: ImageView
    private lateinit var mRecycler: RecyclerView
    private lateinit var mPopupWindow: PopupWindow
    private lateinit var mProcedureListView: ListView
    private val procedureList: ArrayList<String> = ArrayList()

    private var uri: Uri? = null
//    // 根据uri得到图片之后，先转为bitmap进行后续操作，并删除原文件
//    private var bitmap: Bitmap? = null

    private var mItemList = ArrayList<RecyclerItem>()
    private lateinit var uploadManager: UploadManager

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
        initQiniu()
        initPopup()
    }

    /**
     * 从相册获取图片
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun getPicFromAlbum() {
        var canReadTag = true

        // 一般不会执行到此处，因为在进入相机页时已经获取到读取外部存储器权限了
        AndPermission.with(this)
                .runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .onGranted {
                    canReadTag = true
                }
                .onDenied {
                    canReadTag = false
                    Toast.makeText(this,
                            "请手动开启这些权限，否则应用无法正常使用：" +
                                    "${Permission.transformText(this, Permission.READ_EXTERNAL_STORAGE)}",
                            Toast.LENGTH_LONG).show()
                    finish()
                }
                .start()

        if (canReadTag) {
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
                Log.d(TAG, "onActivityResult: 从相册中返回")
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "onActivityResult: resultCode == Activity.RESULT_OK")
                    mImageView.setImageURI(data?.data)
                } else {
                    Log.d(TAG, "onActivityResult: resultCode != Activity.RESULT_OK")
                    Toast.makeText(this, "返回有问题，获取不到图片", Toast.LENGTH_SHORT).show()
                    finish()
                }
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
            EDIT_ACTIVITY_REQUEST_CODE -> {
                val tag = data!!.getIntExtra("tag", 1)
                if (tag == 0) {
                    val bundle = data.extras
                    uri = bundle!!.getParcelable("uri")
                }
                mImageView.setImageURI(uri)
            }
            Filter_ACTIVITY_REQUEST_CODE -> {
                val tag = data!!.getIntExtra("tag", 1)
                if (tag == 0) {
                    val bundle = data.extras
                    uri = bundle!!.getParcelable("uri")
                }
                mImageView.setImageURI(uri)
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
        inflater.inflate(R.menu.puzzle_menu_upload, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.puzzle_menu_upload_save -> {
                return true
            }
            R.id.puzzle_menu_upload_upload -> {
                uploadPic()
                return true
            }
            R.id.puzzle_menu_upload_history -> {
                showPopupWindow()
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
                        startActivityForResult(intent, EDIT_ACTIVITY_REQUEST_CODE)
                    }
                    1 -> cropPhoto(uri!!)
                    2 -> {
                        val intent = Intent()
                        intent.setClass(this@PuzzleActivity, FilterActivity::class.java)
                        intent.putExtra("Uri", uri)
                        startActivityForResult(intent, Filter_ACTIVITY_REQUEST_CODE)
                    }
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

    private fun initQiniu() {
        val config = Configuration.Builder()
                .zone(FixedZone.zone2)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build()
        uploadManager = UploadManager(config)
    }

    private fun uploadPic() {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyyMMdd_hhmmss")
        uploadManager.put(baos.toByteArray(), "PhotoXiu/${dateFormat.format(date)}.jpeg", TOKEN,
                { key, info, response ->
                    if (info!!.isOK) {
                        Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Upload Fail", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("qiniu", "$key,\r\n $info,\r\n $response")
                },
                UploadOptions(null, null, false,
                        UpProgressHandler { key, percent ->
                            Log.i("qiniu", "$key: $percent")
                        }, null)
        )
    }

    private fun initPopup() {
        val view: View = LayoutInflater.from(this).inflate(R.layout.puzzle_popup_window, null)
        mPopupWindow = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, 600, false)

        mPopupWindow.isOutsideTouchable = true
        // 设置PopupWindow是否能响应外部点击事件
        mPopupWindow.isTouchable = true
        // 设置PopupWindow是否能响应点击事件
        mPopupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        // 设置PopupWindow背景

        mProcedureListView = view.findViewById(R.id.puzzle_popup_procedure_list)
    }

    private fun showPopupWindow() {
        procedureList.clear()
        for (procedure in ProcedureUtil.getInstance()) {
            procedureList.add(procedure.toString())
        }
        if (procedureList.size != 0) {
            mProcedureListView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, procedureList)
            mPopupWindow.showAtLocation(window.decorView, Gravity.BOTTOM, 0, 0)
        }
    }
}
