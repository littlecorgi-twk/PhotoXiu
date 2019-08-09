package com.littlecorgi.puzzle.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Route
import com.littlecorgi.puzzle.BaseActivity
import com.littlecorgi.puzzle.R
import com.littlecorgi.puzzle.adapter.RecyclerAdapter
import com.littlecorgi.puzzle.bean.RecyclerItem


@Route(path = "/puzzle/PuzzleActivity")
class PuzzleActivity : BaseActivity() {

    private lateinit var mToolbar: Toolbar
    private lateinit var mImageView: ImageView
    private lateinit var mRecycler: RecyclerView

    private var mItemList = ArrayList<RecyclerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)

        mToolbar = findViewById(R.id.toolbar_activity_puzzle)
        mImageView = findViewById(R.id.imageView_activity_puzzle)
        mRecycler = findViewById(R.id.recycler_activity_puzzle)

        initView()
        initToolbar()
        initRecyclerView()
    }

    private fun initView() {

    }

    private fun initToolbar() {
        setSupportActionBar(mToolbar)
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
