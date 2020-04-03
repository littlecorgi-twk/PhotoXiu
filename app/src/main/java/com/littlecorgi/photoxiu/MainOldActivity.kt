package com.littlecorgi.photoxiu

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.littlecorgi.photoxiu.adapter.RecyclerAdapter
import com.littlecorgi.photoxiu.adapter.ViewpagerAdapter
import com.littlecorgi.photoxiu.bean.RecyclerItemBean
import kotlin.math.abs

@Route(path = "/app/MainOldActivity")
class MainOldActivity : AppCompatActivity() {

    private val toolbar: Toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar)
    }
    private val recyclerView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.recycler_content_scrolling)
    }
    private val viewPager: ViewPager by lazy {
        findViewById<ViewPager>(R.id.banner)
    }
    private val appBar: AppBarLayout by lazy {
        findViewById<AppBarLayout>(R.id.app_bar)
    }
    private val toolbarButtonLayout: ConstraintLayout by lazy {
        findViewById<ConstraintLayout>(R.id.toolbar_button_layout)
    }
    private val cameraButton: ImageView by lazy {
        findViewById<ImageView>(R.id.fab1)
    }
    private val cameraToolbarButton: ImageView by lazy {
        findViewById<ImageView>(R.id.fab1_toolbar)
    }
    private val puzzleButton: ImageView by lazy {
        findViewById<ImageView>(R.id.fab2)
    }
    private val puzzleToolbarButton: ImageView by lazy {
        findViewById<ImageView>(R.id.fab2_toolbar)
    }
    private val retouchButton: ImageView by lazy {
        findViewById<ImageView>(R.id.fab3)
    }
    private val retouchToolbarButton: ImageView by lazy {
        findViewById<ImageView>(R.id.fab3_toolbar)
    }
    private val fab: FloatingActionButton by lazy {
        findViewById<FloatingActionButton>(R.id.fab)
    }

    private var adapter: RecyclerAdapter? = null
    private var viewpagerAdapter: ViewpagerAdapter? = null

    private val viewList = ArrayList<ImageView>()
    private val drawableList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_main_old)

        // 透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.statusBarColor = Color.TRANSPARENT //防止5.x以后半透明影响效果，使用这种透明方式
        }

        // 初始化 ARouter
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        ARouter.openLog()     // 打印日志
        ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init(application)

        setSupportActionBar(toolbar)

        adapter = RecyclerAdapter(initData(), this)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter

        initViewpager()
        initAppBar()
        initButton()
        initFloatingActionBar()
    }

    private fun initData(): List<RecyclerItemBean> {
        val list: ArrayList<RecyclerItemBean> = ArrayList()
        for (i in 0..20) {
            list.add(RecyclerItemBean("小柯基 $i", "河山大好 $i", i.toString()))
        }
        return list
    }

    private fun initViewpager() {
        drawableList.add(R.drawable.app_pic2)
        drawableList.add(R.drawable.app_pic1)
        drawableList.add(R.drawable.app_pic2)
        drawableList.add(R.drawable.app_pic1)

        for (item in 0 until drawableList.size) {
            val iv = ImageView(this)
            iv.setImageResource(drawableList[item])
            iv.adjustViewBounds = true
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            viewList.add(iv)
        }

        viewpagerAdapter = ViewpagerAdapter(viewList)
        viewPager.adapter = viewpagerAdapter
        viewPager.currentItem = 2

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            var currentPosition: Int = 0

            override fun onPageScrollStateChanged(p0: Int) {
                if (p0 == ViewPager.SCROLL_STATE_IDLE) {
                    return
                }
                if (currentPosition == 0) {
                    viewPager.setCurrentItem(viewPager.adapter!!.count - 2, false)
                } else if (currentPosition == viewPager.adapter!!.count - 1) {
                    viewPager.setCurrentItem(1, false)
                }
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                currentPosition = p0
            }
        })

        val scroller = ViewPagerScroller(this)
        scroller.setScrollDuration(2000)
        scroller.initViewPagerScroll(viewPager)
    }

    private fun initAppBar() {
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            when {
                p1 == 0 -> { // 展开状态
                    toolbarButtonLayout.visibility = View.GONE
                }
                abs(p1) >= p0!!.totalScrollRange -> { // 收缩状态
                    toolbarButtonLayout.visibility = View.VISIBLE
                }
                else -> { // 中间状态
                    toolbarButtonLayout.visibility = View.GONE
                }
            }
        })
    }

    private fun initButton() {
        cameraButton.setOnClickListener { ARouter.getInstance().build("/camera/CameraActivity").navigation() }
        cameraToolbarButton.setOnClickListener { ARouter.getInstance().build("/camera/CameraActivity").navigation() }
        puzzleButton.setOnClickListener { ARouter.getInstance().build("/puzzle/PuzzleActivity").navigation() }
        puzzleToolbarButton.setOnClickListener { ARouter.getInstance().build("/puzzle/PuzzleActivity").navigation() }
        retouchButton.setOnClickListener { ARouter.getInstance().build("/retouch/RetouchActivity").navigation() }
        retouchToolbarButton.setOnClickListener { ARouter.getInstance().build("/retouch/RetouchActivity").navigation() }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    private fun initFloatingActionBar() {
        fab.setOnClickListener {
            // startActivity(Intent(this, UploadActivity::class.java))
        }
    }
}
