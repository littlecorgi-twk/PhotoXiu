package com.littlecorgi.puzzle.filter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.littlecorgi.commonlib.BaseActivity
import com.littlecorgi.puzzle.R
import com.littlecorgi.puzzle.adapter.RecyclerAdapter
import com.littlecorgi.puzzle.bean.Procedure
import com.littlecorgi.puzzle.bean.RecyclerItem
import com.littlecorgi.puzzle.util.FilterHelper
import com.littlecorgi.puzzle.util.ProcedureUtil
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.puzzle_activity_filter.*

@Route(path = "/puzzle/FilterActivity")
class FilterActivity : BaseActivity() {

    private var uri: Uri? = null
    private var mItemList = ArrayList<RecyclerItem>()
    private var tag = 1
    private var oldBitmap: Bitmap? = null
    private var tmpBitmap: Bitmap? = null

    companion object {
        private const val TAG = "FilterActivity"
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
        private const val Filter_ACTIVITY_REQUEST_CODE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_activity_filter)

        // 透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.statusBarColor = Color.TRANSPARENT //防止5.x以后半透明影响效果，使用这种透明方式
        }

        val intent = intent
        uri = intent.getParcelableExtra("Uri")
        oldBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        tmpBitmap = oldBitmap

        imageViewActivityFilter.setImageBitmap(oldBitmap)

        initToolbar()
        initRecyclerView()
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
                requestWrite()
                return true
            }
            R.id.puzzle_menu_not_upload_history -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbarActivityFilter)
        toolbarActivityFilter.setNavigationOnClickListener {
            tag = 1
            finished()
        }
    }

    private fun initRecyclerView() {
        initRecyclerItem()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerActivityFilter.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapter(mItemList)
        adapter.setOnItemClickListener(object : RecyclerAdapter.Companion.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        tmpBitmap = FilterHelper.grayScale(oldBitmap!!)
                        imageViewActivityFilter.setImageBitmap(tmpBitmap)
                        ProcedureUtil.getInstance().add(Procedure("grayScale", 0.0F))
                    }
                    1 -> {
                        tmpBitmap = FilterHelper.negativeScale(oldBitmap!!)
                        imageViewActivityFilter.setImageBitmap(tmpBitmap)
                        ProcedureUtil.getInstance().add(Procedure("negativeScale", 0.0F))
                    }
                    2 -> {
                        tmpBitmap = FilterHelper.nostalgic(oldBitmap!!)
                        imageViewActivityFilter.setImageBitmap(tmpBitmap)
                        ProcedureUtil.getInstance().add(Procedure("nostalgic", 0.0F))
                    }
                    3 -> {
                        tmpBitmap = FilterHelper.removeColor(oldBitmap!!)
                        imageViewActivityFilter.setImageBitmap(tmpBitmap)
                        ProcedureUtil.getInstance().add(Procedure("removeColor", 0.0F))
                    }
                    4 -> {
                        tmpBitmap = FilterHelper.highSaturation(oldBitmap!!)
                        imageViewActivityFilter.setImageBitmap(tmpBitmap)
                        ProcedureUtil.getInstance().add(Procedure("highSaturation", 0.0F))
                    }
                    5 -> {
                        tmpBitmap = FilterHelper.redGreenInverted(oldBitmap!!)
                        imageViewActivityFilter.setImageBitmap(tmpBitmap)
                        ProcedureUtil.getInstance().add(Procedure("redGreenInverted", 0.0F))
                    }
                    else ->
                        makeShortToast("1234")
                }
            }
        })
        recyclerActivityFilter.adapter = adapter
    }

    private fun initRecyclerItem() {
        val edit = RecyclerItem("灰度", R.drawable.ic_tune_black_24dp)
        mItemList.add(edit)
        val clip = RecyclerItem("负片", R.drawable.ic_crop_black_24dp)
        mItemList.add(clip)
        val filter = RecyclerItem("怀旧", R.drawable.ic_photo_filter_black_24dp)
        mItemList.add(filter)
        val frames = RecyclerItem("去色", R.drawable.ic_filter_frames_black_24dp)
        mItemList.add(frames)
        val filter1 = RecyclerItem("高饱和度", R.drawable.ic_tune_black_24dp)
        mItemList.add(filter1)
        val filter2 = RecyclerItem("红绿反色", R.drawable.ic_crop_black_24dp)
        mItemList.add(filter2)
    }

    private fun finished() {
        val intent = Intent()
        intent.putExtra("tag", tag)
        if (tag == 0) {
            val uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, tmpBitmap, null, null))
            intent.putExtra("uri", uri)
        }
        setResult(Filter_ACTIVITY_REQUEST_CODE, intent)
        finish()
    }

    /**
     * 请求写权限
     */
    private fun requestWrite() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted {
                    finished()
                }
                .onDenied {
                    makeShortToast("Permission Denied")
                }
                .start()
    }

    override fun onBackPressed() {
        tag = 1
        finished()
    }
}
