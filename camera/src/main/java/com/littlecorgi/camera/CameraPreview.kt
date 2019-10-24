package com.littlecorgi.camera

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

/**
 * 预览类
 */
class CameraPreview(
        context: Context,
        private val mCamera: Camera
) : SurfaceView(context), SurfaceHolder.Callback {

    companion object {
        private const val TAG = "CameraPreview"
    }

    private val mHolder = holder.apply {
        // 安装SurfaceHolder.callback，以便在创建和销毁基础曲面时收到通知。
        addCallback(this@CameraPreview)
        // 不推荐使用的设置，但在3.0之前的Android版本上是必需的
        setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mCamera.apply {
            try {
                setPreviewDisplay(holder)
                startPreview()
            } catch (e: IOException) {
                Log.d(TAG, "Error setting camera preview: ${e.message}")
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        // empty. Take care of releasing the camera preview in your activity.
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        // 如果预览可以更改或旋转，请在此处处理这些事件。
        // 在调整或重新格式化预览之前，请确保停止预览。
        if (mHolder.surface == null)
            return

        // 更改前停止预览
        try {
            mCamera.stopPreview()
        } catch (e: Exception) {
            // 忽略：试图阻止不存在的预览
        }

        // 设置预览大小并在此处进行任何大小调整、旋转或重新格式化更改

        // 使用新设置开始预览
        mCamera.apply {
            try {
                setPreviewDisplay(mHolder)
                startPreview()
            } catch (e: Exception) {
                Log.d(TAG, "Error starting camera preview: ${e.message}")
            }
        }
    }
}