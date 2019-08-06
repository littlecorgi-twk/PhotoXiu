package com.littlecorgi.puzzle.clipimage

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.littlecorgi.puzzle.R
import java.io.FileOutputStream
import java.io.IOException

class ClipImageActivity : AppCompatActivity(), View.OnClickListener {

    private var mClipImageView: ClipImageView? = null
    private var mCancel: TextView? = null
    private var mClip: TextView? = null

    private var mOutput: String? = null
    private var mInput: String? = null
    private var mMaxWidth: Int? = null

    // 图片旋转角度
    private var mDegree: Int? = null
    // 大图被设置前的缩放比
    private var mSampleSize: Int? = null
    private var mSourceWidth: Int? = null
    private var mSourceHeight: Int? = null

    private var mDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clip_image)
        mClipImageView = findViewById(R.id.clip_image_view)
        mCancel = findViewById(R.id.cancel)
        mClip = findViewById(R.id.clip)

        mCancel!!.setOnClickListener(this)
        mClip!!.setOnClickListener(this)

        val clipOptions = ClipOptions.createFromBundle(intent)
        mOutput = clipOptions.getOutputPath()
        mInput = clipOptions.getInputPath()
        mMaxWidth = clipOptions.getMaxWidth()
        mClipImageView!!.setAspect(clipOptions.getAspectX(), clipOptions.getAspectY())
        mClipImageView!!.setTip(clipOptions.getTip()!!)
        mClipImageView!!.setMaxOutputWidth(mMaxWidth!!)

        setImageAndClipParams() // 大图裁切
//        mClipImageView.setImageURI(Uri.fromFile(File(mInput)))
        mDialog = ProgressDialog(this)
        mDialog!!.setMessage(getString(R.string.msg_clipping_image))
    }

    private fun setImageAndClipParams() {
        mClipImageView!!.post {
            mClipImageView!!.setMaxOutputWidth(mMaxWidth!!)

            mDegree = readPictureDegree(mInput!!)

            val isRotate: Boolean = (mDegree == 90) || (mDegree == 270)

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(mInput, options)

            mSourceWidth = options.outWidth
            mSourceHeight = options.outHeight

            // 如果图片被旋转，则宽高度置换
            val w: Int = if (isRotate) options.outHeight else options.outWidth

            // 裁剪是宽高比例3:2，只考虑宽度情况，这里按border宽度的两倍来计算缩放。
            mSampleSize = findBestSample(w, mClipImageView!!.getClipBorder().width())

            options.inJustDecodeBounds = false
            options.inSampleSize = mSampleSize as Int
            options.inPreferredConfig = Bitmap.Config.RGB_565
            val source = BitmapFactory.decodeFile(mInput, options)

            // 解决图片被旋转的问题
            val target: Bitmap?
            if (mDegree == 0) {
                target = source
            } else {
                val matrix = Matrix()
                matrix.postRotate(mDegree!!.toFloat())
                target = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, false)
                if (target != source && !source.isRecycled) {
                    source.recycle()
                }
            }
            mClipImageView!!.setImageBitmap(target)
        }
    }

    override fun onClick(v: View?) {
        val id = v!!.id
        if (id == R.id.cancel) {
            onBackPressed()
        }
        if (id == R.id.clip) {
            clipImage()
        }
    }

    private fun clipImage() {
        if (mOutput != null) {
            mDialog!!.show()
            val task: AsyncTask<Void, Void, Void> = object : AsyncTask<Void, Void, Void>() {
                override fun doInBackground(vararg params: Void): Void? {
                    var fos: FileOutputStream? = null
                    try {
                        fos = FileOutputStream(mOutput)
                        val bitmap = createClippedBitmap()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        if (!bitmap.isRecycled) {
                            bitmap.recycle()
                        }
                        setResult(Activity.RESULT_OK, intent)
                    } catch (e: Exception) {
                        Toast.makeText(this@ClipImageActivity, R.string.msg_could_not_save_photo, Toast.LENGTH_SHORT).show()
                    } finally {
                        if (fos != null) {
                            IOUtils.close(fos)
                        }
                    }
                    return null
                }

                override fun onPostExecute(result: Void?) {
                    mDialog!!.dismiss()
                    finish()
                }
            }
            task.execute()
        } else {
            finish()
        }
    }

    private fun createClippedBitmap(): Bitmap {
        if (mSampleSize!! <= 1) {
            return mClipImageView!!.clip()
        }

        // 获取缩放位移后的矩阵值
        val matrixValues: FloatArray = mClipImageView!!.getClipMatrixValues()
        val scale = matrixValues[Matrix.MSCALE_X]
        val transX = matrixValues[Matrix.MTRANS_X]
        val transY = matrixValues[Matrix.MTRANS_Y]

        // 获取在显示的图片中裁剪的位置
        val border = mClipImageView!!.getClipBorder()
        val cropX = ((-transX + border.left) / scale) * mSampleSize!!
        val cropY = ((-transY + border.top) / scale) * mSampleSize!!
        val cropWidth = (border.width() / scale) * mSampleSize!!
        val cropHeight = (border.height() / scale) * mSampleSize!!

        // 获取在旋转之前的裁剪位置
        val srcRect = RectF(cropX, cropY, cropX + cropWidth, cropY + cropHeight)
        val clipRect = getRealRect(srcRect)

        val ops = BitmapFactory.Options()
        val outputMatrix = Matrix()

        outputMatrix.setRotate(mDegree!!.toFloat())
        // 如果裁剪之后的图片宽高任然太大，则进行缩小
        if (mMaxWidth!! > 0 && cropWidth > mMaxWidth!!) {
            ops.inSampleSize = findBestSample(cropWidth.toInt(), mMaxWidth!!)

            val outputScale = mMaxWidth!! / (cropWidth / ops.inSampleSize)
            outputMatrix.postScale(outputScale, outputScale)
        }

        // 裁剪
        var decoder: BitmapRegionDecoder? = null
        return try {
            decoder = BitmapRegionDecoder.newInstance(mInput, false)
            val source = decoder.decodeRegion(clipRect, ops)
            recycleImageViewBitmap()
            Bitmap.createBitmap(source, 0, 0, source.width, source.height, outputMatrix, false)
        } catch (e: java.lang.Exception) {
            mClipImageView!!.clip()
        } finally {
            if (decoder != null && !decoder.isRecycled) {
                decoder.recycle()
            }
        }
    }

    private fun getRealRect(srcRect: RectF): Rect {
        when (mDegree) {
            90 -> return Rect(srcRect.top.toInt(), (mSourceHeight!! - srcRect.right).toInt(),
                    srcRect.bottom.toInt(), (mSourceHeight!! - srcRect.left).toInt())
            180 ->
                return Rect((mSourceWidth!! - srcRect.right).toInt(),
                        ((mSourceHeight!! - srcRect.bottom).toInt()),
                        ((mSourceWidth!! - srcRect.left).toInt()),
                        ((mSourceHeight!! - srcRect.top).toInt()))
            270 ->
                return Rect(((mSourceWidth!! - srcRect.bottom).toInt()),
                        srcRect.left.toInt(), ((mSourceWidth!! - srcRect.top).toInt()),
                        srcRect.right.toInt())
            else ->
                return Rect(srcRect.left.toInt()
                        , srcRect.top.toInt(), srcRect.right.toInt(), srcRect.bottom.toInt())
        }
    }

    private fun recycleImageViewBitmap() {
        mClipImageView!!.post(Runnable { mClipImageView!!.setImageBitmap(null) })
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, intent)
        super.onBackPressed()
    }

    companion object {

        /**
         * 计算最好的采样率
         */
        private fun findBestSample(origin: Int, target: Int): Int {
            var sample: Int = 1
            var out: Int = origin / 2
            while (out > target) {
                sample *= 2
                out /= 2
            }
            return sample
        }

        /**
         * 读取图片属性：旋转的角度
         *
         * @param path 图片绝对路径
         * @return degree 旋转角度
         */
        public fun readPictureDegree(path: String): Int {
            var degree: Int = 0
            try {
                val exifInterface = ExifInterface(path)
                when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return degree
        }

        public fun prepare(): ClipOptions {
            return ClipOptions()
        }

        public class ClipOptions {
            private var aspectX = 0
            private var aspectY = 0
            private var maxWidth = 0
            private var tip: String? = "TestTip因为源码中找不到哪儿对tip有初始化"
            private var inputPath: String? = null
            private var outputPath: String? = null

            public fun aspectX(aspectX: Int): ClipOptions {
                this.aspectX = aspectX
                return this
            }

            public fun aspectY(aspectY: Int): ClipOptions {
                this.aspectY = aspectY
                return this
            }

            public fun maxWidth(maxWidth: Int): ClipOptions {
                this.maxWidth = maxWidth
                return this
            }

            public fun tip(tip: String): ClipOptions {
                this.tip = tip
                return this
            }

            public fun inputPath(path: String): ClipOptions {
                this.inputPath = path
                return this
            }

            public fun outputPath(path: String): ClipOptions {
                this.outputPath = path
                return this
            }

            public fun getAspectX(): Int {
                return aspectX
            }

            public fun getAspectY(): Int {
                return aspectY
            }

            public fun getMaxWidth(): Int {
                return maxWidth
            }

            public fun getTip(): String? {
                return tip
            }

            public fun getInputPath(): String? {
                return inputPath
            }

            public fun getOutputPath(): String? {
                return outputPath
            }

            public fun startForResult(activity: Activity, requestCode: Int) {
                checkValues()
                val intent = Intent(activity, ClipImageActivity::class.java)
                intent.putExtra("aspectX", aspectX)
                intent.putExtra("aspectY", aspectY)
                intent.putExtra("maxWidth", maxWidth)
                intent.putExtra("tip", tip)
                intent.putExtra("inputPath", inputPath)
                intent.putExtra("outputPath", outputPath)
                activity.startActivityForResult(intent, requestCode)
            }

            private fun checkValues() {
                if (TextUtils.isEmpty(inputPath)) {
                    throw IllegalArgumentException("The input path could not be empty")
                }
                if (TextUtils.isEmpty(outputPath)) {
                    throw IllegalArgumentException("The output path could not be empty")
                }
            }

            companion object {
                public fun createFromBundle(intent: Intent): ClipOptions {
                    return ClipOptions()
                            .aspectX(intent.getIntExtra("aspectX", 1))
                            .aspectY(intent.getIntExtra("aspectY", 1))
                            .maxWidth(intent.getIntExtra("maxWidth", 0))
                            .tip(intent.getStringExtra("tip"))
                            .inputPath(intent.getStringExtra("inputPath"))
                            .outputPath(intent.getStringExtra("outputPath"))
                }
            }
        }
    }
}
