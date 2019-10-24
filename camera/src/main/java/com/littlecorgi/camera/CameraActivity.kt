package com.littlecorgi.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.util.Log
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Route
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@Route(path = "/camera/CameraActivity")
class CameraActivity : BaseActivity() {

    companion object {
        private const val TAG = "CameraActivity"
    }

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private var mediaRecorder: MediaRecorder? = null
    private val mPicture = Camera.PictureCallback { data, _ ->
        val pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: kotlin.run {
            Log.d(TAG, "Error creating media file, check storage permissions")
            return@PictureCallback
        }

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity_camera)

        mCamera = getCameraInstance()

        mPreview = mCamera?.let {
            CameraPreview(this, it)
        }

        mPreview?.also {
            val preview: FrameLayout = findViewById(R.id.camera_camera_preview)
            preview.addView(it)
        }
    }

    override fun onPause() {
        super.onPause()
        releaseMediaRecorder()
        releaseCamera()
    }

    private fun releaseMediaRecorder() {
        mediaRecorder?.reset()
        mediaRecorder?.release()
        mediaRecorder = null
        mCamera?.lock()
    }

    private fun releaseCamera() {
        mCamera?.release()
        mCamera = null
    }

    /**
     * 检测设备是否有相机硬件
     * 如果有返回True，否则返回False
     */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /**
     * 获取Camera对象实例
     *
     * 注意：使用时，请务必检查是否有异常Camera.open()。如果没有检查相机是否在使用中或不存在，将导致系统关闭您的应用程序。
     *
     * 将访问具有多个摄像头的设备上的第一个背面摄像头
     */
    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // 相机不可用(正在使用或不存在)
            null // returns null if camera is unavailable
        }
    }

    /**
     * 获取相机支持的功能
     */
    private fun getCameraParameters(camera: Camera): Camera.Parameters {
        return camera.parameters
    }

    /**
     * 获取相机信息
     */
    private fun getCameraInfo(cameraId: Int): Camera.CameraInfo {
        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)
        return cameraInfo
    }

    private fun prepareVideoRecorder(): Boolean {
        val mediaRecorder = MediaRecorder()
        mCamera?.let { camera ->
            camera.unlock()

            mediaRecorder.run {
                setCamera(camera)

                setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)

                setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))

                setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString())

                setPreviewDisplay(mPreview?.holder?.surface)

                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)

                return try {
                    prepare()
                    true
                } catch (e: IllegalStateException) {
                    Log.d(TAG, "IllegalStateException preparing MediaRecorder: ${e.message}")
                    releaseMediaRecorder()
                    false
                } catch (e: IOException) {
                    Log.d(TAG, "IOException preparing MediaRecorder: ${e.message}")
                    releaseMediaRecorder()
                    false
                }
            }
        }
        return false
    }

    private fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    @SuppressLint("SimpleDateFormat")
    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp"
        )

        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d("MyCameraApp", " failed to create directory")
                    return null
                }
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return when (type) {
            MEDIA_TYPE_IMAGE -> {
                File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
            }
            MEDIA_TYPE_VIDEO -> {
                File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
            }
            else -> null
        }
    }
}
