package com.littlecorgi.camera

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView

/**
 * 预览类
 */
class CameraPreview @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : TextureView(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, width / 3 * 4)
    }
}