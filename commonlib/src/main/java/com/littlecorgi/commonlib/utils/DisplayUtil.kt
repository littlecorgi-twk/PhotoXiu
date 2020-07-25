package com.littlecorgi.commonlib.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager


object DisplayUtil {

    var sContext: Context? = null

    /**
     * 屏幕宽度
     */
    var sScreenWidth = 0

    /**
     * 屏幕高度
     */
    var sScreenHeight = 0

    /**
     * 获取屏幕尺寸与密度
     */
    fun getDisplayMetrics(): DisplayMetrics {
        return if (sContext == null) {
            Resources.getSystem().displayMetrics
        } else {
            sContext!!.resources.displayMetrics
        }
    }

    /**
     * TypedValue官方源码中的算法 任意单位转换为PX单位
     *
     * @param unit           TypedValue.COMPLEX_UNIT_DIP
     * @param value          对应单位的值
     * @param displayMetrics 密度
     */
    fun applyDimension(unit: Int, value: Float, displayMetrics: DisplayMetrics): Float {
        when (unit) {
            TypedValue.COMPLEX_UNIT_PX ->
                return value
            TypedValue.COMPLEX_UNIT_DIP ->
                return value * displayMetrics.density
            TypedValue.COMPLEX_UNIT_SP ->
                return value * displayMetrics.scaledDensity
            TypedValue.COMPLEX_UNIT_PT ->
                return value * displayMetrics.xdpi * (1.0f / 72)
            TypedValue.COMPLEX_UNIT_IN ->
                return value * displayMetrics.xdpi
            TypedValue.COMPLEX_UNIT_MM ->
                return value * displayMetrics.xdpi * (1.0f / 25.4f)
        }
        return 0f
    }

    /**
     * px 转 dp
     *
     * @param pxValue px
     * @return dp
     */
    fun pxToDp(pxValue: Float): Float {
        val displayMetrics = getDisplayMetrics()
        return pxValue / displayMetrics.density
    }

    /**
     * dp 转 px
     *
     * @param dpValue dp
     * @return px
     */
    fun dpToPx(dpValue: Float): Float {
        val displayMetrics = getDisplayMetrics()
        return applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, displayMetrics)
    }

    /**
     * px 转 sp
     *
     * @param pxValue px
     * @return sp
     */
    fun pxToSp(pxValue: Float): Float {
        val displayMetrics = getDisplayMetrics()
        return pxValue / displayMetrics.scaledDensity
    }

    /**
     * sp 转 px
     *
     * @param spValue sp
     * @return px
     */
    fun spToPx(spValue: Float): Float {
        val displayMetrics = getDisplayMetrics()
        return applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, displayMetrics)
    }

    /**
     * 获得屏幕宽度
     *
     * @return
     */
    fun getScreenWidth(): Int {
        val wm: WindowManager = sContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    /**
     * 获得屏幕高度
     *
     * @return
     */
    fun getScreenHeight(): Int {
        val wm: WindowManager = sContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.heightPixels
    }
}