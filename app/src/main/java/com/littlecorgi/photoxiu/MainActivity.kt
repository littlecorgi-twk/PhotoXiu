package com.littlecorgi.photoxiu

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.AppBarLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.littlecorgi.photoxiu.adapter.RecyclerAdapter
import com.littlecorgi.photoxiu.adapter.ViewpagerAdapter
import com.littlecorgi.photoxiu.bean.RecyclerItemBean
import kotlin.math.abs

@Route(path = "/app/MainActivity")
class MainActivity : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private var recyclerView: RecyclerView? = null
    private var viewPager: ViewPager? = null
    private var appBar: AppBarLayout? = null
    private var toolbarButtonLayout: ConstraintLayout? = null
    private var puzzleButton: ImageView? = null
    private var puzzleToolbarButton: ImageView? = null

    private var adapter: RecyclerAdapter? = null
    private var viewpagerAdapter: ViewpagerAdapter? = null

    private val viewList = ArrayList<ImageView>()
    private val drawableList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ARouter.openLog()     // 打印日志
        ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init(application) // 尽可能早，推荐在Application中初始化

        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.banner)
        recyclerView = findViewById(R.id.recycler_content_scrolling)
        appBar = findViewById(R.id.app_bar)
        toolbarButtonLayout = findViewById(R.id.toolbar_button_layout)
        puzzleButton = findViewById(R.id.fab2)
        puzzleToolbarButton = findViewById(R.id.fab2_toolbar)

        setSupportActionBar(toolbar)
        toolbar!!.title = ""

        adapter = RecyclerAdapter(initData())
        recyclerView!!.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView!!.adapter = adapter

        initViewpager()
        initAppBar()
        initButton()
    }

    private fun initData(): List<RecyclerItemBean> {
        val list: ArrayList<RecyclerItemBean> = ArrayList()
        for (i in 0..50) {
            list.add(RecyclerItemBean("小柯基 $i", "河山大好 $i", i.toString()))
        }
        return list
    }

    private fun initViewpager() {
        drawableList.add(R.drawable.pic2)
        drawableList.add(R.drawable.pic1)
        drawableList.add(R.drawable.pic2)
        drawableList.add(R.drawable.pic1)

        for (item in 0 until drawableList.size) {
            val iv = ImageView(this)
            iv.setImageResource(drawableList[item])
            iv.adjustViewBounds = true
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            viewList.add(iv)
        }

        viewpagerAdapter = ViewpagerAdapter(viewList)
        viewPager!!.adapter = viewpagerAdapter
        viewPager!!.currentItem = 2

        viewPager!!.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            var currentPosition: Int = 0

            override fun onPageScrollStateChanged(p0: Int) {
                if (p0 == ViewPager.SCROLL_STATE_IDLE) {
                    return
                }
                if (currentPosition == 0) {
                    viewPager!!.setCurrentItem(viewPager!!.adapter!!.count - 2, false)
                } else if (currentPosition == viewPager!!.adapter!!.count - 1) {
                    viewPager!!.setCurrentItem(1, false)
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
        scroller.initViewPagerScroll(viewPager!!)
    }

    private fun initAppBar() {
        appBar!!.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
                var state: Int = 0
                when {
                    p1 == 0 -> { // 展开状态
                        toolbarButtonLayout!!.visibility = View.GONE
                    }
                    abs(p1) >= p0!!.totalScrollRange -> { // 收缩状态
                        toolbarButtonLayout!!.visibility = View.VISIBLE
                    }
                    else -> { // 中间状态
                        toolbarButtonLayout!!.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun initButton() {
        puzzleButton!!.setOnClickListener { ARouter.getInstance().build("/puzzle/PuzzleActivity").navigation() }
        puzzleToolbarButton!!.setOnClickListener { ARouter.getInstance().build("/puzzle/PuzzleActivity").navigation() }
    }
}
