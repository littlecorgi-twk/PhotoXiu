package com.littlecorgi.puzzle.clipimage

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import com.littlecorgi.puzzle.R
import kotlin.math.sqrt

class ClipImageView(context: Context, attrs: AttributeSet?) : ImageView(context, attrs),
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private var mPaint: Paint? = null // 画笔

    private var mMaskColor: Int = 0 // 遮罩层颜色
    private var mAspectX: Int = 0 // 裁剪框宽的大小（从属性上读到的整型值）
    private var mAspectY: Int = 0 // 裁剪框高的大小（同上）
    private var mTipText: String? = null // 提示文字
    private var mClipPadding: Int = 0 // 裁剪框相对于控件的内边距

    private var mScaleMax: Float = 4.0f // 图片最大缩放大小
    private var mScaleMin: Float = 2.0f // 图片最小缩放大小

    /**
     * 初始化时的缩放比例
     */
    private var mInitScale: Float = 1.0f

    /**
     * 用于存放矩阵
     */
    private val mMatrixValues = FloatArray(9)

    /**
     * 缩放的手势检查
     */
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private val mScaleMatrix = Matrix()

    /**
     * 用于双击
     */
    private lateinit var mGestureDetector: GestureDetector
    private var isAutoScale: Boolean = false

    private var mLastX: Float = 0.0f
    private var mLastY: Float = 0.0f

    private var isCanDrag: Boolean = false
    private var lastPointerCount: Int = 0

    private val mClipBorder = Rect()
    private var mMaxOutputWidth: Int = 0

    private var mDrawCircleFlag: Boolean = true
    private var mRoundCorner: Float

    constructor(context: Context) : this(context, null)

    init {
        scaleType = ScaleType.MATRIX
        mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                if (isAutoScale)
                    return true
                var x = e!!.x
                var y = e.y
                if (getScale()!! < mScaleMin) {
                    this@ClipImageView.postDelayed(AutoScaleRunnable(mScaleMax, x, y), 16 )
                } else {
                    this@ClipImageView.postDelayed(AutoScaleRunnable(mInitScale, x, y), 16)
                }
                isAutoScale = true

                return true
            }
        })
        mScaleGestureDetector = ScaleGestureDetector(context, this)
        this.setOnTouchListener(this)

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = Color.WHITE

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ClipImageView)
        mAspectX = ta.getInteger(R.styleable.ClipImageView_civWidth, 1)
        mAspectY = ta.getInteger(R.styleable.ClipImageView_civHeight, 1)
        mClipPadding = ta.getDimensionPixelSize(R.styleable.ClipImageView_civClipPadding, 0)
        mTipText = ta.getInteger(R.styleable.ClipImageView_civTipText, 0).toString()
        mMaskColor = ta.getColor(R.styleable.ClipImageView_civMaskColor, 0xB2000000.toInt())
        mDrawCircleFlag = ta.getBoolean(R.styleable.ClipImageView_civClipCircle, false)
        mRoundCorner = ta.getDimension(R.styleable.ClipImageView_civClipRoundCorner, 0F)
        val textSize = ta.getDimension(R.styleable.ClipImageView_civTipTextSize, 24F)
        mPaint!!.textSize = textSize
        ta.recycle()

        mPaint!!.isDither = true
    }

    private inner class AutoScaleRunnable(targetScale: Float, x: Float, y: Float) : Runnable {

        val BIGGER: Float = 1.07f
        val SMALLER: Float = 0.93f

        private var mTargetScale: Float = targetScale
        private var tmpScale: Float

        /**
         * 缩放的中心
         */
        private var x: Float = x
        private var y: Float = y

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         */
        init {
            tmpScale = if (getScale()!! < mTargetScale) {
                BIGGER
            } else {
                SMALLER
            }
        }

        override fun run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y)
            checkBorder()
            imageMatrix = mScaleMatrix

            val currentScale: Float? = getScale()
            // 如果值在合法范围之内，继续缩放
            if (((tmpScale > 1f) && (currentScale!! < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale!!))) {
                this@ClipImageView.postDelayed(this, 16)
            } else {
                // 设置为目标的缩放比例
                val deltaScale: Float = mTargetScale / currentScale!!
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y)
                checkBorder()
                imageMatrix = mScaleMatrix
                isAutoScale = false
            }
        }
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        val scale = getScale()
        var scaleFactor = detector!!.scaleFactor

        if (drawable == null) {
            return true
        }

        /**
         * 缩放的范围控制
         */
        if ((scale!! < mScaleMax && scaleFactor > 1.0f)
                || (scale > mInitScale && scaleFactor < 1.0f)) {
            /**
             * 缩放阈值最小值判断
             */
            if (scaleFactor * scale < mInitScale) {
                scaleFactor = mInitScale / scale
            }
            if (scaleFactor * scale > mScaleMax) {
                scaleFactor = mScaleMax / scale
            }
            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
            checkBorder()
            imageMatrix = mScaleMatrix
        }
        return true
    }

    /**
     * 根据当前图片的Matrix获得图片的资源
     */
    private fun getMatrixRecF(): RectF {
        val matrix = mScaleMatrix
        val rect = RectF()
        val d = drawable
        if (null != d) {
            rect.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
            matrix.mapRect(rect)
        }
        return rect
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (mGestureDetector.onTouchEvent(event))
            return true
        mScaleGestureDetector!!.onTouchEvent(event)

        var x: Float = 0f
        var y: Float = 0f
        // 拿到触摸点的个数
        val pointerCount: Int = event!!.pointerCount

        // 得到多个触摸点的x与y均值
        for (i in 0..pointerCount) {
            x += event.getX(i)
            y += event.getY(i)
        }
        x /= pointerCount
        y /= pointerCount

        /**
         * 每当触摸点发生变化时，重置mLastX、mLastY
         */
        if (pointerCount != lastPointerCount) {
            isCanDrag = false
            mLastX = x
            mLastY = y
        }

        lastPointerCount = pointerCount
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                var dx = x - mLastY
                var dy = y - mLastY

                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy)
                }
                if (isCanDrag) {
                    if (drawable != null) {

                        val recF = getMatrixRecF()
                        // 如果宽度小于屏幕宽度，则禁止左右移动
                        if (recF.width() <= mClipBorder.width()) {
                            dx = 0f
                        }

                        // 如果高度小于屏幕高度，则禁止上下移动
                        if (recF.height() <= mClipBorder.height()) {
                            dy = 0f
                        }
                        mScaleMatrix.postTranslate(dx, dy)
                        checkBorder()
                        imageMatrix = mScaleMatrix
                    }
                }
                mLastX = x
                mLastY = y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> lastPointerCount = 0
        }
        return true
    }

    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public fun getScale(): Float? {
        mScaleMatrix.getValues(mMatrixValues)
        return mMatrixValues[Matrix.MSCALE_X]
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        updateBorder()
    }

    private fun updateBorder() {
        val width = width
        val height = height
        mClipBorder.left = mClipPadding
        mClipBorder.right = width - mClipPadding
        val borderHeight = mClipBorder.width() * mAspectY / mAspectX
        if (mDrawCircleFlag) { // 如果是圆形,宽高比例是1:1
            val borderTempHeight = mClipBorder.width() * 1 / 1
            mClipBorder.top = (height - borderTempHeight) / 2
            mClipBorder.bottom = mClipBorder.top + borderTempHeight
        } else { // 如果不是圆形,根据宽高比例
            mClipBorder.top = (height - borderHeight) / 2;
            mClipBorder.bottom = mClipBorder.top + borderHeight;
        }
    }

    public fun setAspect(aspectX: Int, aspectY: Int) {
        mAspectX = aspectX
        mAspectY = aspectY
    }

    public fun setTip(tip: String) {
        mTipText = tip
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        postResetImageMatrix()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        postResetImageMatrix()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        postResetImageMatrix()
    }

    /**
     * 这里没有使用post方式,因为图片会有明显的从初始位置移动到需要缩放的位置
     */
    private fun postResetImageMatrix() {
        if (width != 0) {
            resetImageMatrix()
        } else {
            post(Runnable { resetImageMatrix() })
        }
    }

    /**
     * 垂直方向与View的边矩
     */
    public fun resetImageMatrix() {
        val d = drawable ?: return
        val dWidth = d.intrinsicWidth
        val dHeight = d.intrinsicHeight

        val cWidth = mClipBorder.width()
        val cHeight = mClipBorder.height()

        val vWidth = width
        val vHeight = height

        val scale: Float
        val dx: Float
        val dy: Float

        if (dWidth * cHeight > cWidth * dHeight) {
            scale = (cHeight / dHeight).toFloat()
        } else {
            scale = (cWidth / dWidth).toFloat()
        }

        dx = (vWidth - dWidth * scale) * 0.5f
        dy = (vHeight - dHeight * scale) * 0.5f

        mScaleMatrix.setScale(scale, scale)
        mScaleMatrix.postTranslate(dx + 0.5f, dy + 0.5f)

        imageMatrix = mScaleMatrix

        mInitScale = scale
        mScaleMin = mInitScale * 2
        mScaleMax = mInitScale * 4
    }

    /**
     * 裁剪图片
     */
    public fun clip(): Bitmap {
        val drawable = drawable
        val originalBirmap = (drawable as BitmapDrawable).bitmap

        val matrixValues: FloatArray = FloatArray(9)
        mScaleMatrix.getValues(matrixValues)
        val scale = matrixValues[Matrix.MSCALE_X] * drawable.intrinsicWidth / originalBirmap.width
        val transX = matrixValues[Matrix.MTRANS_X]
        val transY = matrixValues[Matrix.MTRANS_Y]

        val cropX = (-transX + mClipBorder.left) / scale
        val cropY = (-transY + mClipBorder.top) / scale
        val cropWidth = mClipBorder.width() / scale
        val cropHeight = mClipBorder.height() / scale

        var outputMatrix: Matrix? = null
        if (mMaxOutputWidth > 0 && cropWidth > mMaxOutputWidth) {
            val outputScale = mMaxOutputWidth / cropWidth
            outputMatrix = Matrix()
            outputMatrix.setScale(outputScale, outputScale)
        }

        return Bitmap.createBitmap(originalBirmap, cropX.toInt(), cropY.toInt(), cropWidth.toInt(),
                cropHeight.toInt(), outputMatrix, false)
    }

    /**
     * 边界检查
     */
    private fun checkBorder() {
        val rect = getMatrixRecF()
        var deltaX: Float = 0f
        var deltaY: Float = 0f

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= mClipBorder.width()) {
            if (rect.left > mClipBorder.left) {
                deltaX = -rect.left + mClipBorder.left
            }

            if (rect.right < mClipBorder.right) {
                deltaX = mClipBorder.right - rect.right
            }
        }

        if (rect.height() >= mClipBorder.height()) {
            if (rect.top > mClipBorder.top) {
                deltaY = -rect.top + mClipBorder.top
            }

            if (rect.bottom < mClipBorder.bottom) {
                deltaY = mClipBorder.bottom - rect.bottom
            }
        }

        mScaleMatrix.postTranslate(deltaX, deltaY)
    }

    /**
     * 是否是拖动行为
     */
    private fun isCanDrag(dx: Float, dy: Float): Boolean {
        return sqrt(((dx * dx) + (dy * dy)).toDouble()) >= 0
    }

    public fun getClipBorder(): Rect {
        return mClipBorder
    }

    public fun setMaxOutputWidth(maxOutputWidth: Int) {
        mMaxOutputWidth = maxOutputWidth
    }

    public fun getClipMatrixValues(): FloatArray {
        val matrixValues = FloatArray(9)
        mScaleMatrix.getValues(matrixValues)
        return matrixValues
    }

    /**
     * 参考showTipsView的做法
     */
    public fun drawRectangleOrCircle(canvas: Canvas) {
        val bitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
        val temp = Canvas(bitmap)
        val transparentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val porterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        transparentPaint.color = Color.TRANSPARENT
        temp.drawRect(0F, 0F, temp.width.toFloat(), temp.height.toFloat(), mPaint)
        transparentPaint.xfermode = porterDuffXfermode
        if (mDrawCircleFlag) { // 画圆
            val cx = mClipBorder.left + mClipBorder.width() / 2f
            val cy = mClipBorder.top + mClipBorder.height() / 2f
            val radius = mClipBorder.height() / 2f
            temp.drawCircle(cx, cy, radius, transparentPaint)
        } else { // 画矩形（可以设置矩形的圆角）
            val rectF = RectF(mClipBorder.left.toFloat(), mClipBorder.top.toFloat(), mClipBorder.right.toFloat(), mClipBorder.bottom.toFloat())
            temp.drawRoundRect(rectF, mRoundCorner, mRoundCorner, transparentPaint)
        }
        canvas.drawBitmap(bitmap, 0F, 0F, null)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width = width
        val height = height

        mPaint!!.color = mMaskColor
        mPaint!!.style = Paint.Style.FILL
//        canvas!!.drawRect(0F, 0F, width.toFloat(), mClipBorder.top.toFloat(), mPaint)
//        canvas.drawRect(0F, mClipBorder.bottom.toFloat(), width.toFloat(), height.toFloat(), mPaint)
//        canvas.drawRect(0F, mClipBorder.top.toFloat(), mClipBorder.left.toFloat(), mClipBorder.bottom.toFloat(), mPaint)
//        canvas.drawRect(mClipBorder.right.toFloat(), mClipBorder.top.toFloat(), width.toFloat(), mClipBorder.bottom.toFloat(), mPaint)
//
//        mPaint!!.color = Color.WHITE
        mPaint!!.strokeWidth = 1F
        drawRectangleOrCircle(canvas!!)
//        mPaint!!.style = Paint.Style.STROKE
//
//        canvas.drawRect(mClipBorder.left.toFloat(), mClipBorder.top.toFloat(), mClipBorder.right.toFloat(), mClipBorder.bottom.toFloat(), mPaint);
        if (mTipText != null) {
            val textWidth = mPaint!!.measureText(mTipText)
            val startX = (width - textWidth) / 2
            val fm = mPaint!!.fontMetrics
            val startY = mClipBorder.bottom + mClipBorder.top / 2 - (fm.descent - fm.ascent) / 2
            mPaint!!.style = Paint.Style.FILL
            canvas.drawText(mTipText, startX, startY, mPaint)
        }
    }
}
