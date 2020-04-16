package com.littlecorgi.photoxiu.view.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.littlecorgi.photoxiu.R
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-16 17:06
 */
class ShootButton(context: Context, attrs: AttributeSet?, def: Int) : View(context, attrs, def) {
    companion object {
        const val MODE_CENTER_CIRCLE = 0
        const val MODE_CENTER_RECT = 1

        private const val WIDTH_DEFAULT = 100 // 默认宽
        private const val HEIGHT_DEFAULT = 100 // 默认高
    }

    private var mContext: Context? = null
    // 画笔
    private var mPaint: Paint
    // 按下颜色矩阵
    private var colorFilter: ColorMatrixColorFilter? = null
    // 圆环区域
    private var mCircleOval: RectF? = null
    // 中心区域
    private var mCenterOval: RectF? = null
    // 圆环内边半径
    private var circleMinRadius = 0f
    // 圆环宽度
    private var mCircleWidth = 0f
    // 圆环颜色
    private var mCircleColor = 0
    // 中心图标颜色
    private var mCenterColor = 0
    // 中心图标-圆与圆环间距
    private var mCenterCirclePadding = 0f
    // 中心图标-方块与圆环间距
    private var mCenterRectPadding = 0f
    // 中心圆角矩形的圆角半径
    private var mCenterRectRadius = 0f
    // 中心显示模式
    private var mCenterMode = 0
    // 进度最大值
    private var mMaxProgress = 0
    // 进度
    private var mProgress = 0

    private var isPress = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        mContext = context
        //获取自定义属性和默认值
        val mTypedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ShootButton)
        mCircleWidth = mTypedArray.getDimension(R.styleable.ShootButton_circleWidth, 10f)
        mCircleColor = mTypedArray.getColor(R.styleable.ShootButton_circleColor, Color.WHITE)
        mCenterColor = mTypedArray.getColor(R.styleable.ShootButton_centerColor, Color.RED)
        mCenterCirclePadding = mTypedArray.getDimension(R.styleable.ShootButton_centerCirclePadding, 10f)
        mCenterRectPadding = mTypedArray.getDimension(R.styleable.ShootButton_centerRectPadding, 10f)
        mCenterRectRadius = mTypedArray.getDimension(R.styleable.ShootButton_centerRectRadius, 10f)
        mMaxProgress = mTypedArray.getInt(R.styleable.ShootButton_maxProgress, 100)
        mProgress = mTypedArray.getInt(R.styleable.ShootButton_progress, 100)
        mCenterMode = mTypedArray.getInt(R.styleable.ShootButton_centerMode, MODE_CENTER_CIRCLE)
        mTypedArray.recycle()
        mPaint = Paint()
        mPaint.isAntiAlias = true
        colorFilter = ColorMatrixColorFilter(floatArrayOf(
                0.6f, 0f, 0f, 0f, 0f,
                0f, 0.6f, 0f, 0f, 0f,
                0f, 0f, 0.6f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
        ))
        mCircleOval = RectF()
        mCenterOval = RectF()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width: Int
        val height: Int
        width = if (widthMode == MeasureSpec.EXACTLY) { // match/具体数值
            widthSize
        } else { // wrap/un
            val widthDest = WIDTH_DEFAULT + paddingLeft + paddingRight
            min(widthDest, widthSize)
        }
        height = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            val heightDest = HEIGHT_DEFAULT + paddingTop + paddingBottom
            min(heightDest, heightSize)
        }
        // 确定圆环画布区域
        val minLength = min(width, height)
        mCircleOval!![width / 2 - minLength / 2 + mCircleWidth / 2 + paddingLeft, height / 2 - minLength / 2 + mCircleWidth / 2 + paddingTop, width / 2 + minLength / 2 - mCircleWidth / 2 - paddingRight] = height / 2 + minLength / 2 - mCircleWidth / 2 - paddingBottom
        // 圆环内边半径
        circleMinRadius = (mCircleOval!!.right - mCircleOval!!.left - mCircleWidth) / 2
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isPress) {
            mPaint.colorFilter = colorFilter
        } else {
            mPaint.colorFilter = null
        }
        // 圆环
        mPaint.color = mCircleColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mCircleWidth
        canvas.drawArc(mCircleOval!!, 0f, 360f, false, mPaint)

        // 中心图标
        if (mCenterMode == MODE_CENTER_CIRCLE) { // 中心图标-圆
            mPaint.color = mCenterColor
            mPaint.style = Paint.Style.FILL
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(),
                    circleMinRadius - mCenterCirclePadding, mPaint)
        } else { // 中心图标-方块
            mPaint.color = mCenterColor
            mPaint.style = Paint.Style.FILL
            val width = sqrt((circleMinRadius - mCenterRectPadding.toDouble()).pow(2.0) / 2).toInt()
            mCenterOval!![getWidth() / 2 - width.toFloat(), height / 2 - width.toFloat(), getWidth() / 2 + width.toFloat()] = height / 2 + width.toFloat()
            canvas.drawRoundRect(mCenterOval!!, mCenterRectRadius, mCenterRectRadius, mPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPress = true
                invalidate()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                isPress = false
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    fun getCircleWidth(): Float {
        return mCircleWidth
    }

    fun setCircleWidth(circleWidth: Float) {
        mCircleWidth = circleWidth
        invalidate()
    }

    fun getCircleColor(): Int {
        return mCircleColor
    }

    fun setCircleColor(circleColor: Int) {
        mCircleColor = circleColor
        invalidate()
    }

    fun setCircleColorRes(circleColorRes: Int) {
        mCircleColor = ContextCompat.getColor(mContext!!, circleColorRes)
        invalidate()
    }

    fun getCenterColor(): Int {
        return mCenterColor
    }

    fun setCenterColor(centerColor: Int) {
        mCenterColor = centerColor
        invalidate()
    }

    fun setCenterColorRes(centerColorRes: Int) {
        mCenterColor = ContextCompat.getColor(mContext!!, centerColorRes)
        invalidate()
    }

    fun getCenterRectRadius(): Float {
        return mCenterRectRadius
    }

    fun setCenterRectRadius(centerRectRadius: Float) {
        mCenterRectRadius = centerRectRadius
        invalidate()
    }

    fun getCenterMode(): Int {
        return mCenterMode
    }

    fun setCenterMode(centerMode: Int) {
        mCenterMode = centerMode
        invalidate()
    }

    fun getMaxProgress(): Int {
        return mMaxProgress
    }

    fun setMaxProgress(maxProgress: Int) {
        mMaxProgress = maxProgress
        invalidate()
    }

    fun getProgress(): Int {
        return mProgress
    }

    fun setProgress(progress: Int) {
        mProgress = progress
        invalidate()
    }

    fun getmCenterCirclePadding(): Float {
        return mCenterCirclePadding
    }

    fun setmCenterCirclePadding(mCenterCirclePadding: Float) {
        this.mCenterCirclePadding = mCenterCirclePadding
        invalidate()
    }

    fun getmCenterRectPadding(): Float {
        return mCenterRectPadding
    }

    fun setmCenterRectPadding(mCenterRectPadding: Float) {
        this.mCenterRectPadding = mCenterRectPadding
        invalidate()
    }
}