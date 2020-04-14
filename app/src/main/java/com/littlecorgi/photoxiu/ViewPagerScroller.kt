package com.littlecorgi.photoxiu

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager


class ViewPagerScroller(context: Context, interpolator: Interpolator?, flywheel: Boolean) : Scroller(context, interpolator, flywheel) {
    private var mScrollDuration = 2000 // 滑动速度

    /**
     * 设置速度速度
     *
     * @param duration
     */
    fun setScrollDuration(duration: Int) {
        this.mScrollDuration = duration
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, interpolator: Interpolator?): this(context,interpolator,false)

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration)
    }

    fun initViewPagerScroll(viewPager: ViewPager) {
        try {
            val mScroller = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            mScroller.set(viewPager, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}