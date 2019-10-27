package com.littlecorgi.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import java.io.File
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class CameraFragment : Fragment(), View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        private const val TAG = "CameraFragment"
        private const val MAX_PREVIEW_WIDTH = 1920
        private const val MAX_PREVIEW_HEIGHT = 1080

        // 预览状态
        private const val STATE_PREVIEW = 0
        // 对焦锁定状态
        private const val STATE_WAITING_LOCK = 1
        // 拍照前的曝光状态
        private const val STATE_WAITING_PRECAPTURE = 2
        // 除了拍照之外其他事件的曝光状态
        private const val STATE_WAITING_NON_PRECAPTURE = 3
        // 已经拍好照片
        private const val STATE_PICTURE_TAKEN = 4

        private val ORIENTATIONS = SparseIntArray()

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }

    /**
     * 图片文件
     */
    private lateinit var file: File
    private lateinit var changeCameraButton: ImageButton
    private lateinit var textureView: AutoFitTextureView
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    /**
     * CameraID
     */
    private lateinit var cameraId: String
    /**
     * 打开的摄像头的引用
     */
    private var cameraDevice: CameraDevice? = null
    /**
     * 用于设置用后置摄像头还是前置摄像头，true为后置
     */
    private var lensFacingFlag = true
    /**
     * 用于从SurfaceView接收图像
     */
    private var imageReader: ImageReader? = null
    /**
     * onImageAvailable的回调接口
     */
    private val onImageAvailableListener = ImageReader.OnImageAvailableListener {
        backgroundHandler?.post(ImageSaver(it.acquireNextImage(), file))
    }
    /**
     * 传感器方向
     */
    private var sensorOrientation = 0
    /**
     * 预览尺寸
     */
    private lateinit var previewSize: Size
    /**
     * 是否支持闪光灯
     */
    private var flashSupported = false
    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) = Unit

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = true

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            openCamera(width, height)
        }
    }
    /**
     * 相机的状态
     */
    private var state = STATE_PREVIEW
    /**
     * 确保在关闭相机后才退出程序的信号量
     */
    private val cameraOpenCloseLock = Semaphore(1)
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            this@CameraFragment.cameraDevice = camera
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            camera.close()
            this@CameraFragment.cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            onDisconnected(camera)
            this@CameraFragment.activity?.finish()
        }
    }
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private var captureSession: CameraCaptureSession? = null
    private lateinit var previewRequest: CaptureRequest
    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {
        private fun process(result: CaptureResult) {
            when (state) {
                STATE_PREVIEW -> Unit
                STATE_WAITING_LOCK -> capturePicture(result)
                STATE_WAITING_PRECAPTURE -> {
                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        state = STATE_WAITING_NON_PRECAPTURE
                    }
                }
                STATE_WAITING_NON_PRECAPTURE -> {
                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        state = STATE_PICTURE_TAKEN
                        captureStillPicture()
                    }
                }
            }
        }

        private fun capturePicture(result: CaptureResult) {
            val afState = result.get(CaptureResult.CONTROL_AF_STATE)
            if (afState == null) {
                captureStillPicture()
            } else if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                    || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                // CONTROL_AE_STATE can be null on some devices
                val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                    state = STATE_PICTURE_TAKEN
                    captureStillPicture()
                } else {
                    runPrecaptureSequence()
                }
            }
        }

        override fun onCaptureProgressed(session: CameraCaptureSession,
                                         request: CaptureRequest,
                                         partialResult: CaptureResult) {
            process(partialResult)
        }

        override fun onCaptureCompleted(session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult) {
            process(result)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.camera_fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.camera_picture).setOnClickListener(this)
        changeCameraButton = view.findViewById(R.id.camera_change_camera)
        changeCameraButton.setOnClickListener(this)
        textureView = view.findViewById(R.id.camera_texture)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()

        if (textureView.isAvailable) {
            openCamera(textureView.width, textureView.height)
        } else {
            textureView.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.camera_picture -> lockFocus()
            R.id.camera_change_camera -> {
                lensFacingFlag = if (lensFacingFlag) {
                    changeCameraButton.setImageResource(R.drawable.camera_ic_camera_rear_black_24dp)
                    false
                } else {
                    changeCameraButton.setImageResource(R.drawable.camera_ic_camera_front_black_24dp)
                    true
                }
                closeCamera()
                stopBackgroundThread()
                startBackgroundThread()
                openCamera(textureView.width, textureView.height)
            }
        }
    }

    private fun openCamera(width: Int, height: Int) {
        val permission = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
            return
        }
        setUpCameraOutputs(width, height)
        configureTransform(width, height)
        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }
    }

    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
            imageReader?.close()
            imageReader = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance("This sample needs camera permission.")
                        .show(childFragmentManager, FRAGMENT_DIALOG)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun setUpCameraOutputs(width: Int, height: Int) {
        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                // 得到相机的参数
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // 得到相机的朝向，是前置还是后置
                val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (lensFacingFlag) {
                    if (cameraDirection != null && cameraDirection == CameraCharacteristics.LENS_FACING_FRONT) {
                        continue
                    }
                } else {
                    if (cameraDirection != null && cameraDirection == CameraCharacteristics.LENS_FACING_BACK) {
                        continue
                    }
                }

                // 得到流配置
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        ?: continue

                val largest = Collections.max(
                        listOf(*map.getOutputSizes(ImageFormat.JPEG)),
                        CompareSizeByArea()
                )
                imageReader = ImageReader.newInstance(largest.width, largest.height, ImageFormat.JPEG, 2).apply {
                    setOnImageAvailableListener(onImageAvailableListener, backgroundHandler)
                }

                val displayRotation = activity!!.windowManager.defaultDisplay.rotation

                sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                val swappedDimensions = areDimensionsSwapped(displayRotation)
                val displaySize = Point()
                activity!!.windowManager.defaultDisplay.getSize(displaySize)
                val rotatedPreviewWidth = if (swappedDimensions) height else width
                val rotatedPreviewHeight = if (swappedDimensions) width else height
                var maxPreviewWidth = if (swappedDimensions) displaySize.y else displaySize.x
                var maxPreviewHeight = if (swappedDimensions) displaySize.x else displaySize.y

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) maxPreviewWidth = MAX_PREVIEW_WIDTH
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) maxPreviewHeight = MAX_PREVIEW_HEIGHT

                previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
                        rotatedPreviewWidth, rotatedPreviewHeight,
                        maxPreviewWidth, maxPreviewHeight,
                        largest)

                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setAspectRatio(previewSize.width, previewSize.height)
                } else {
                    textureView.setAspectRatio(previewSize.height, previewSize.width)
                }

                // 闪光灯
                flashSupported = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                this.cameraId = cameraId
                return
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: NullPointerException) {
            ErrorDialog.newInstance("This device doesn't support Camera2 API.")
                    .show(childFragmentManager, FRAGMENT_DIALOG)
        }
    }

    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        activity ?: return
        val rotation = activity!!.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0F, 0F, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0F, 0F, previewSize.height.toFloat(), previewSize.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            val scale = Math.max(
                    viewHeight.toFloat() / previewSize.height,
                    viewWidth.toFloat() / previewSize.width
            )
            with(matrix) {
                setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                postScale(scale, scale, centerX, centerY)
                postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
            }
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180F, centerX, centerY)
        }
        textureView.setTransform(matrix)
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread?.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun areDimensionsSwapped(displayRotation: Int): Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (sensorOrientation == 90 || sensorOrientation == 270) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (sensorOrientation == 0 || sensorOrientation == 180) {
                    swappedDimensions = true
                }
            }
            else -> {
                Log.e(TAG, "Display rotation is invalid: $displayRotation")
            }
        }
        return swappedDimensions
    }

    /**
     * 给定相机支持的“大小”选项，选择最小的一个，其大小至少与相应的纹理视图大小相同，
     * 且最多与相应的最大大小相同，且其纵横比与指定值匹配。
     * 如果不存在这样的大小，则选择最大的一个，最大的大小与各自的最大大小相同，其纵横比与指定值匹配。
     *
     * @param choices           相机为预期输出类支持的大小列表
     * @param textureViewWidth  相对于传感器坐标的纹理视图宽度
     * @param textureViewHeight 相对于传感器坐标的纹理视图高度
     * @param maxWidth          可选择的最大宽度
     * @param maxHeight         可选择的最大高度
     * @param aspectRatio       纵横比
     * @return The optimal `Size`, or an arbitrary one if none were big enough
     */
    private fun chooseOptimalSize(
            choices: Array<Size>,
            textureViewWidth: Int,
            textureViewHeight: Int,
            maxWidth: Int,
            maxHeight: Int,
            aspectRatio: Size
    ): Size {
        val bigEnough = ArrayList<Size>()
        val notBigEnough = ArrayList<Size>()

        val w = aspectRatio.width
        val h = aspectRatio.height
        for (option in choices) {
            if (option.width <= maxWidth && option.height <= maxHeight && option.height == option.width * h / w) {
                if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                    bigEnough.add(option)
                } else {
                    notBigEnough.add(option)
                }
            }
        }

        if (bigEnough.size > 0) {
            return Collections.min(bigEnough, CompareSizeByArea())
        } else if (notBigEnough.size > 0) {
            return Collections.max(notBigEnough, CompareSizeByArea())
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size")
            return choices[0]
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture
            texture.setDefaultBufferSize(previewSize.width, previewSize.height)
            val surface = Surface(texture)

            previewRequestBuilder = cameraDevice!!.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW
            )
            previewRequestBuilder.addTarget(surface)

            cameraDevice?.createCaptureSession(listOf(surface, imageReader?.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            activity!!.showToast("Failed")
                        }

                        override fun onConfigured(session: CameraCaptureSession) {
                            if (cameraDevice == null) return

                            captureSession = session
                            try {
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                                setAutoFlash(previewRequestBuilder)

                                previewRequest = previewRequestBuilder.build()
                                captureSession?.setRepeatingRequest(previewRequest,
                                        captureCallback, backgroundHandler)
                            } catch (e: CameraAccessException) {
                                Log.e(TAG, e.toString())
                            }
                        }
                    }, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun setAutoFlash(requestBuilder: CaptureRequest.Builder) {
        if (flashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
        }
    }

    private fun capturePicture(result: CaptureResult) {
        val afState = result.get(CaptureResult.CONTROL_AF_STATE)
        if (afState == null) {
            captureStillPicture()
        } else if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
            val asState = result.get(CaptureResult.CONTROL_AE_STATE)
            if (asState == null || asState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                state = STATE_PICTURE_TAKEN
                captureStillPicture()
            } else {
                runPrecaptureSequence()
            }
        }
    }

    private fun captureStillPicture() {
        try {
            if (activity == null || cameraDevice == null) return
            val rotation = activity!!.windowManager.defaultDisplay.rotation

            // This is the CaptureRequest.Builder that we use to take a picture.
            val captureBuilder = cameraDevice?.createCaptureRequest(
                    CameraDevice.TEMPLATE_STILL_CAPTURE)?.apply {
                addTarget(imageReader?.surface)

                // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
                // We have to take that into account and rotate JPEG properly.
                // For devices with orientation of 90, we return our mapping from ORIENTATIONS.
                // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
                set(CaptureRequest.JPEG_ORIENTATION,
                        (ORIENTATIONS.get(rotation) + sensorOrientation + 270) % 360)

                // Use the same AE and AF modes as the preview.
                set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            }?.also { setAutoFlash(it) }

            val captureCallback = object : CameraCaptureSession.CaptureCallback() {

                override fun onCaptureCompleted(session: CameraCaptureSession,
                                                request: CaptureRequest,
                                                result: TotalCaptureResult) {
                    activity!!.showToast("Saved: $file")
                    Log.d(TAG, file.toString())
                    unlockFocus()
                }
            }

            captureSession?.apply {
                stopRepeating()
                abortCaptures()
                capture(captureBuilder?.build(), captureCallback, null)
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun runPrecaptureSequence() {
        try {
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START)
            state = STATE_WAITING_PRECAPTURE
            captureSession?.capture(previewRequestBuilder.build(), captureCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START)
            // Tell #captureCallback to wait for the lock.
            state = STATE_WAITING_LOCK
            captureSession?.capture(previewRequestBuilder.build(), captureCallback,
                    backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }

    }

    private fun unlockFocus() {
        try {
            // Reset the auto-focus trigger
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
            setAutoFlash(previewRequestBuilder)
            captureSession?.capture(previewRequestBuilder.build(), captureCallback,
                    backgroundHandler)
            // After this, the camera will go back to the normal state of preview.
            state = STATE_PREVIEW
            captureSession?.setRepeatingRequest(previewRequest, captureCallback,
                    backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }
}
