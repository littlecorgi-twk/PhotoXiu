package com.littlecorgi.puzzle.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.widget.Button
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Route
import com.littlecorgi.puzzle.BaseActivity
import com.littlecorgi.puzzle.R
import com.yalantis.ucrop.UCrop
import java.io.File

@Route(path = "/puzzle/uCropActivity")
class uCropActivity : BaseActivity() {

    //相册请求码
    private val ALBUM_REQUEST_CODE = 1
    //相机请求码
    private val CAMERA_REQUEST_CODE = 2
    //剪裁请求码
    private val CROP_REQUEST_CODE = 3
    //调用照相机返回图片文件
    private var tempFile: File? = null

    private var mOutputPath: String? = null

    private var mButtonPuzzle: Button? = null
    private var mImageViewShow: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_u_crop)

        //建议在application 的onCreate()的方法中调用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }

        /**
         * 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
         */
        if (Build.VERSION.SDK_INT >= 23) {
            val REQUEST_CODE_CONTACT = 101
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //验证是否许可权限
            for (str in permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT)
                    return
                }
            }
        }

        mButtonPuzzle = findViewById(R.id.button_activity_puzzle)
        mImageViewShow = findViewById(R.id.iv_activity_puzzle_show)

        mOutputPath = File(externalCacheDir, "chosen.jpg").path

        mButtonPuzzle!!.setOnClickListener { getPicFromAlbm() }
    }

    /**
     * 从相机获取图片
     */
    private fun getPicFromCamera() {
        tempFile = File(Environment.getExternalStorageDirectory().path, System.currentTimeMillis().toString() + ".jpg")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果在Android7.0以上,使用FileProvider获取Uri
            intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(this, "com.littlecorgi.puzzle", tempFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
        } else { //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
        }
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    /**
     * 从相册获取图片
     */
    private fun getPicFromAlbm() {

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

            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), ALBUM_REQUEST_CODE)
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
                    val uri = data!!.data
                    cropPhoto(uri)
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = UCrop.getOutput(data!!)
                    mImageViewShow!!.setImageURI(resultUri)
                }
            }
            UCrop.RESULT_ERROR -> {
                val cropError = UCrop.getError(data!!)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
