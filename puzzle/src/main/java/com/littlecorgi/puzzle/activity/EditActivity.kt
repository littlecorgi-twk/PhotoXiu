package com.littlecorgi.puzzle.activity

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.littlecorgi.commonlib.BaseActivity
import com.littlecorgi.puzzle.R
import com.littlecorgi.puzzle.adapter.RecyclerAdapter
import com.littlecorgi.puzzle.bean.Procedure
import com.littlecorgi.puzzle.bean.RecyclerItem
import com.littlecorgi.puzzle.util.ImageHelper
import com.littlecorgi.puzzle.util.ProcedureUtil
import kotlinx.android.synthetic.main.puzzle_activity_edit.*


@Route(path = "/puzzle/EditActivity")
class EditActivity : BaseActivity(), SeekBar.OnSeekBarChangeListener {

    companion object {
        private const val TAG = "EditActivity"
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
        private const val EDIT_ACTIVITY_REQUEST_CODE = 2
    }

    private var oldBitmap: Bitmap? = null
    private var newBitmap: Bitmap? = null
    private var tmpBitmap: Bitmap? = null

    private var mHue = 0.0f
    private var mSaturation = 1f
    private var mLum = 1f
    private var midValue = 0f

    private var uri: Uri? = null

    private var mItemList = ArrayList<RecyclerItem>()

    private var tag = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_activity_edit)

        val intent = intent
        uri = intent.getParcelableExtra("Uri")
        oldBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        tmpBitmap = oldBitmap
        newBitmap = Bitmap.createBitmap(oldBitmap!!.width, oldBitmap!!.height, Bitmap.Config.ARGB_8888)

        midValue = hueSeekBar!!.max * 1.0f / 2

        setSupportActionBar(toolbarActivityEdit)
        toolbarActivityEdit.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbarActivityEdit.setNavigationOnClickListener {
            tag = 1
            finished()
        }
        lumSeekBar!!.setOnSeekBarChangeListener(this)
        hueSeekBar!!.setOnSeekBarChangeListener(this)
        saturationSeekBar!!.setOnSeekBarChangeListener(this)
        imageViewActivityEdit!!.setImageBitmap(oldBitmap)

        lumSeekBar.visibility = View.VISIBLE
        hueSeekBar.visibility = View.GONE
        saturationSeekBar.visibility = View.GONE

        initRecycler()
    }

    private fun initRecycler() {
        initRecyclerItem()
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerActivityEdit.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapter(mItemList)
        adapter.setOnItemClickListener(object : RecyclerAdapter.Companion.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        lumSeekBar.visibility = View.VISIBLE
                        hueSeekBar.visibility = View.GONE
                        saturationSeekBar.visibility = View.GONE
                    }
                    1 -> {
                        lumSeekBar.visibility = View.GONE
                        hueSeekBar.visibility = View.VISIBLE
                        saturationSeekBar.visibility = View.GONE
                    }
                    2 -> {
                        lumSeekBar.visibility = View.GONE
                        hueSeekBar.visibility = View.GONE
                        saturationSeekBar.visibility = View.VISIBLE
                    }
                    else ->
                        makeShortToast("1234")
                }
            }
        })
        recyclerActivityEdit.adapter = adapter
    }

    private fun initRecyclerItem() {
        val light = RecyclerItem("亮度", R.drawable.ic_light_black_24dp)
        mItemList.add(light)
        val tonality = RecyclerItem("色调", R.drawable.ic_tonality_black_24dp)
        mItemList.add(tonality)
        val filter = RecyclerItem("饱和度", R.drawable.ic_iso_black_24dp)
        mItemList.add(filter)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar!!.id) {
            R.id.hueSeekBar -> {
                mHue = (progress - midValue) * 1.0F / midValue * 180
            }
            R.id.saturationSeekBar -> {
                mSaturation = progress * 1.0F / midValue
            }
            R.id.lumSeekBar -> {
                mLum = progress * 1.0F / midValue
            }
        }
        tmpBitmap = ImageHelper.handleImageEffect(oldBitmap!!, newBitmap!!, mHue, mSaturation, mLum)
        imageViewActivityEdit!!.setImageBitmap(tmpBitmap)
//        when (seekBar!!.id) {
//            R.id.hueSeekBar -> {
//                mHue = (progress - MID_VALUE) * 1.0F / MID_VALUE * 180
//                oldBitmap = ImageHelper.handleHueImageEffect(oldBitmap!!, newBitmap!!, mHue)
//                imageViewActivityEdit!!.setImageBitmap(oldBitmap)
//            }
//            R.id.saturationSeekBar -> {
//                mSaturation = progress * 1.0F / MID_VALUE
//                oldBitmap = ImageHelper.handleSaturationImageEffect(oldBitmap!!, newBitmap!!, mSaturation)
//                imageViewActivityEdit!!.setImageBitmap(oldBitmap)
//            }
//            R.id.lumSeekBar -> {
//                mLum = progress * 1.0F / MID_VALUE
//                oldBitmap = ImageHelper.handleLumImageEffect(oldBitmap!!, newBitmap!!, mLum)
//                imageViewActivityEdit!!.setImageBitmap(oldBitmap)
//            }
//        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.puzzle_menu_not_upload, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.puzzle_menu_not_upload_finish -> {
                tag = 0
                if (mHue != 0.0F)
                    ProcedureUtil.getInstance().add(Procedure("hue", mHue))
                if (mSaturation != 1F)
                    ProcedureUtil.getInstance().add(Procedure("saturation", mSaturation))
                if (mLum != 1F)
                    ProcedureUtil.getInstance().add(Procedure("lum", mLum))
                requestWrite()
                return true
            }
            R.id.puzzle_menu_not_upload_history -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun finished() {
        Log.d(TAG, "finished: tag=$tag")
        val intent = Intent()
        if (tag == 0) {
            val uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, tmpBitmap, null, null))
            intent.putExtra("uri", uri)
        }
        intent.putExtra("tag", tag)
        setResult(EDIT_ACTIVITY_REQUEST_CODE, intent)
        finish()
    }

    /**
     * 请求写权限
     */
    private fun requestWrite() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
        } else {
            finished()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finished()
            } else {
                makeShortToast("Permission Denied")
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        tag = 1
        finished()
    }
}
