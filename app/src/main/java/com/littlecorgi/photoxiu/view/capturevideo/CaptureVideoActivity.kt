package com.littlecorgi.photoxiu.view.capturevideo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PictureCallback
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.littlecorgi.photoxiu.ViewModelFactory
import com.littlecorgi.photoxiu.utils.Utils
import com.littlecorgi.photoxiu.view.publishvideo.PublishVideoActivity
import com.littlecorgi.photoxiu.view.view.ShootButton
import com.littlecorgi.photoxiu.viewModel.CaptureVideoViewModel
import com.littlecorgi.photoxiu.R
import com.littlecorgi.photoxiu.databinding.AppActivityCaptureVideoBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.abs
import kotlin.math.min

/**
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-06 15:21
 */
class CaptureVideoActivity : AppCompatActivity() {

    companion object {
        private val TAG = CaptureVideoActivity::class.java.simpleName
        private const val DEGREE_90 = 90
        private const val DEGREE_180 = 180
        private const val DEGREE_270 = 270
        private const val DEGREE_360 = 360
        private const val MSG_OPEN_AUTO_FOCUS = 101
        private const val MSG_RECORD_PROGRESS = 102
        // 当录制时间达到15s时
        private const val MSG_FINISH_RECORD = 103

        // 计时器的最大值，也就是视频的最大录制时间，当到此事件时会自动停止录制
        private const val TIMER_MAX: Long = 15 * 1000
        // 计时器的时间间隔
        private const val TIMER_INTERVAL: Long = 10
    }

    private lateinit var mBinding: AppActivityCaptureVideoBinding
    private lateinit var mCaptureVideoViewModel: CaptureVideoViewModel

    private val mMainHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_OPEN_AUTO_FOCUS -> {
                    openAutoFocus()
                    mCamera!!.setDisplayOrientation(90)
                }
                MSG_FINISH_RECORD -> {
                    // 设置标志位
                    isRecording = false
                    isRecordStart = true
                    //停止录制，并释放MediaRecorder资源
                    stopCapture()
                    // 将录制按钮恢复成未录制状态
                    mBinding.btnRecord.setCenterMode(ShootButton.MODE_CENTER_CIRCLE)
                    mBinding.btnRecord.setCenterColorRes(R.color.app_colorAccent)
                }
            }
        }
    }

    private var mCamera: Camera? = null
    private var mFile: File? = null
    // 默认打开的相机朝向
    private var cameraFacingType = CameraInfo.CAMERA_FACING_FRONT
    private lateinit var mTimer: DouyinTimer
    private var isRecording = false
    // 判断是不是第一次开始录制，主要是用于判断录制按钮，
    // 如果是第一次录制则为true，如果不是则为false，此时点击录制按钮只是暂停而不是终止录制
    private var isRecordStart = true
    private var isFacing = false
    private var rotationDegree = 0
    private var mCameraId = 0
    // 代表已经录制了的时间
    private var hasCapturedTime: Long = 0
    private lateinit var surfaceHolder: SurfaceHolder

    override fun onBackPressed() {
        super.onBackPressed()
        if (mFile != null) {
            if (!mFile!!.delete()) {
                mCaptureVideoViewModel.setToastContent("文件删除失败")
            } else {
                mCaptureVideoViewModel.setToastContent("文件删除成功")
            }
        }
        //停止录制，并释放MediaRecorder资源
        stopCapture()
        isRecordStart = true
        isRecording = false
        finish()
    }

    override fun onPause() {
        super.onPause()
        releaseCameraAndPreview()
    }

    override fun onResume() {
        super.onResume()
        mCamera = getCamera(mCameraId)
        createSurfaceView()
        // 开启相机自动对焦，延时是因为担心执行这行是Camera还没打开
        mMainHandler.sendEmptyMessageDelayed(MSG_OPEN_AUTO_FOCUS, 500)
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mBinding = DataBindingUtil.setContentView(this, R.layout.app_activity_capture_video)
        mCaptureVideoViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(CaptureVideoViewModel::class.java)

        if (checkCameraHardware(this)) {
            val cameraNums = Camera.getNumberOfCameras()
            for (i in 0 until cameraNums) {
                val info = CameraInfo()
                Camera.getCameraInfo(i, info)
                if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                    mCamera = getCamera(i)
                    mCameraId = i
                    break
                }
            }
            Log.d(TAG, "onCreate: ${cameraNums}个相机,打开${mCameraId}号相机")
        } else {
            finish()
        }

        // 开启相机自动对焦，延时是因为担心执行这行是Camera还没打开
        mMainHandler.sendEmptyMessageDelayed(MSG_OPEN_AUTO_FOCUS, 500)

        createSurfaceView()
        /*
         * 录制按钮
         *
         * 如果是第一次录制视频，则isRecording=false，isRecordStart=true，此时会开始录制，则isRecording设为true，isRecordStart设为false
         * 如果是录制过程中按下按钮，则isRecording=true，isRecordStart=false，此时会判断SDK是否大于N，
         * 如果大于等于，则暂停录制，并且将isRecording设为false；否则停止录制，并将isRecording设为false，isRecordStart设为true
         */
        mBinding.btnRecord.setOnClickListener {
            if (isRecording) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mMediaRecorder!!.pause()
                } else {
                    mCaptureVideoViewModel.setToastContent("不支持暂停录制，录制停止")
                    //停止录制，并释放MediaRecorder资源
                    stopCapture()
                    isRecordStart = true
                }
                isRecording = false
                mBinding.btnRecord.setCenterMode(ShootButton.MODE_CENTER_CIRCLE)
                mBinding.btnRecord.setCenterColorRes(R.color.app_colorAccent)
                mTimer.cancel()
            } else {
                if (isRecordStart) {
                    if (prepareVideoRecorder()) {
                        mMediaRecorder!!.start()
                        isRecordStart = false
                        Log.d(TAG, "onCreate: 开始录制")
                        mBinding.btnRecord.setCenterMode(ShootButton.MODE_CENTER_RECT)
                        mBinding.btnRecord.setCenterColorRes(R.color.app_colorAccent)
                        mBinding.ivFinish.visibility = View.VISIBLE
                        mMainHandler.sendEmptyMessage(MSG_RECORD_PROGRESS)
                        isRecording = true
                        mTimer = DouyinTimer(TIMER_MAX - hasCapturedTime, TIMER_INTERVAL)
                        mTimer.start()
                    } else {
                        releaseMediaRecorder()
                        Log.d(TAG, "onCreate: 录制失败：VideoRecorder未就绪")
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mMediaRecorder!!.resume()
                    }
                    mBinding.btnRecord.setCenterMode(ShootButton.MODE_CENTER_RECT)
                    mBinding.btnRecord.setCenterColorRes(R.color.app_colorAccent)
                    isRecording = true
                    mTimer = DouyinTimer(TIMER_MAX - hasCapturedTime, TIMER_INTERVAL)
                    mTimer.start()
                }

            }
        }
        // 翻转摄像头按钮
        mBinding.ivChangeCameraFacing.setOnClickListener {
            if (isFacing) {
                for (i in 0 until Camera.getNumberOfCameras()) {
                    val info = CameraInfo()
                    Camera.getCameraInfo(i, info)
                    if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                        releaseCameraAndPreview()
                        mCamera = getCamera(i)
                    }
                }
                val xrp = resources.getXml(R.drawable.app_ic_camera_rear_black_30dp)
                mBinding.ivChangeCameraFacing.setImageDrawable(Drawable.createFromXml(resources, xrp))
                isFacing = false
            } else {
                for (i in 0 until Camera.getNumberOfCameras()) {
                    val info = CameraInfo()
                    Camera.getCameraInfo(i, info)
                    if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                        releaseCameraAndPreview()
                        mCamera = getCamera(i)
                    }
                }
                val xrp = resources.getXml(R.drawable.app_ic_camera_front_black_30dp)
                mBinding.ivChangeCameraFacing.setImageDrawable(Drawable.createFromXml(resources, xrp))
                isFacing = true
            }
            startPreview(surfaceHolder)
            // 开启相机自动对焦，延时是因为担心执行这行是Camera还没打开
            mMainHandler.sendEmptyMessageDelayed(MSG_OPEN_AUTO_FOCUS, 500)
        }
        mBinding.ivFinish.setOnClickListener {
            //停止录制，并释放MediaRecorder资源
            stopCapture()
            isRecordStart = true
            isRecording = false

            val intent = Intent(this, PublishVideoActivity::class.java)
            intent.putExtra("VideoFile", mFile.toString())
            Log.d(TAG, mFile.toString())
            startActivity(intent)
        }
        mBinding.ivCloseRecorde.setOnClickListener {
            if (mFile != null) {
                if (!mFile!!.delete()) {
                    mCaptureVideoViewModel.setToastContent("文件删除失败")
                } else {
                    mCaptureVideoViewModel.setToastContent("文件删除成功")
                }
            }
            //停止录制，并释放MediaRecorder资源
            stopCapture()
            isRecordStart = true
            isRecording = false

            finish()
        }
        mBinding.progressBarRecorde.max = TIMER_MAX.toInt()
        subscribeUi()
    }

    private fun createSurfaceView() {
        val param = mBinding.surface.layoutParams
//        param.height = 200

        param.height = getScreenWidth() * 16 / 9
        mBinding.surface.layoutParams = param
        surfaceHolder = mBinding.surface.holder
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                startPreview(surfaceHolder)
            }

            override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {}
            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {}
        })
    }

    private fun subscribeUi() {
        mCaptureVideoViewModel.toastContent.observe(this, Observer { toastContext ->
            Toast.makeText(this, toastContext, Toast.LENGTH_SHORT).show()
        })
    }

    private fun openAutoFocus(): Boolean {
        val params = mCamera!!.parameters
        val focusModes = params.supportedFocusModes
        return if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            Log.d(TAG, "onCreate: 自动对焦功能可用")
            // Autofocus mode is supported
            // set the focus mode
            params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
            params.flashMode = Camera.Parameters.FLASH_MODE_AUTO
            // set Camera parameters
            mCamera!!.parameters = params
            true
        } else {
            false
        }
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            true
        } else {
            mCaptureVideoViewModel.setToastContent("对不起，您的设备不支持Camera")
            false
        }
    }

    private fun getCamera(position: Int): Camera {
        cameraFacingType = position
        if (mCamera != null) {
            releaseCameraAndPreview()
        }
        val cam = Camera.open(position)
        rotationDegree = getCameraDisplayOrientation(position)
        cam.setDisplayOrientation(rotationDegree)
        return cam
    }

    private fun getCameraDisplayOrientation(cameraId: Int): Int {
        val info = CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = windowManager.defaultDisplay
                .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = DEGREE_90
            Surface.ROTATION_180 -> degrees = DEGREE_180
            Surface.ROTATION_270 -> degrees = DEGREE_270
            else -> {
            }
        }
        var result: Int
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360
            result = (DEGREE_360 - result) % DEGREE_360 // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360
        }
        return result
    }

    private fun releaseCameraAndPreview() {
        mCamera!!.stopPreview()
        mCamera!!.release()
        mCamera = null
    }

    var size: Camera.Size? = null
    private fun startPreview(holder: SurfaceHolder) {
        try {
            mCamera!!.setPreviewDisplay(holder)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mCamera!!.startPreview()
    }

    private var mMediaRecorder: MediaRecorder? = null
    private fun prepareVideoRecorder(): Boolean {
        mMediaRecorder = MediaRecorder()
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera!!.unlock()
        mMediaRecorder!!.setCamera(mCamera)
        // Step 2: Set sources
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder!!.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
        // 设置角度
        mMediaRecorder!!.setOrientationHint(90)
        // Step 4: Set output file
        mFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_VIDEO)
        mMediaRecorder!!.setOutputFile(mFile.toString())
        // Step 5: Set the preview output
        mMediaRecorder!!.setPreviewDisplay(mBinding.surface!!.holder.surface)
        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder!!.prepare()
        } catch (e: IllegalStateException) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.message)
            releaseMediaRecorder()
            return false
        } catch (e: IOException) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.message)
            releaseMediaRecorder()
            return false
        }
        return true
    }

    private fun releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder!!.reset() // clear recorder configuration
            mMediaRecorder!!.release() // release the recorder object
            mMediaRecorder = null
            mCamera!!.lock() // lock camera for later use
        }
    }

    private val mPicture = PictureCallback label@{ data: ByteArray?, camera: Camera? ->
        val pictureFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE) ?: return@label
        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data!!)
            fos.close()
        } catch (e: IOException) {
            Log.d("mPicture", "Error accessing file: " + e.message)
        }
        mCamera!!.startPreview()
    }

    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val aspectTolerance = 0.1
        val targetRatio = h.toDouble() / w
        if (sizes == null) {
            return null
        }
        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE
        val targetHeight = min(w, h)
        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (abs(ratio - targetRatio) > aspectTolerance) {
                continue
            }
            if (abs(size.height - targetHeight) < minDiff) {
                optimalSize = size
                minDiff = abs(size.height - targetHeight).toDouble()
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE
            for (size in sizes) {
                if (abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size
                    minDiff = abs(size.height - targetHeight).toDouble()
                }
            }
        }
        return optimalSize
    }

    private fun stopCapture() {
        //停止录制，并释放MediaRecorder资源
        mMediaRecorder!!.stop()
        releaseMediaRecorder()
    }

    //获取运行屏幕宽度
    private fun getScreenWidth(): Int {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    inner class DouyinTimer(millisInFuture: Long,
                            countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            mMainHandler.sendEmptyMessage(MSG_FINISH_RECORD)
        }

        override fun onTick(millisUntilFinished: Long) {
            hasCapturedTime = TIMER_MAX - millisUntilFinished
            val progress = hasCapturedTime / TIMER_MAX * 100
            mBinding.progressBarRecorde.progress = hasCapturedTime.toInt()
        }

    }
}