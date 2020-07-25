package com.littlecorgi.commonlib.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import com.littlecorgi.commonlib.utils.UriUtils.formatUri
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author tianweikang
 */
object Utils {
    fun convertUriToPath(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val imgPathSel = formatUri(context, uri)
            if (!TextUtils.isEmpty(imgPathSel)) {
                return imgPathSel
            }
        }
        val schema = uri.scheme
        if (TextUtils.isEmpty(schema) || ContentResolver.SCHEME_FILE == schema) {
            return uri.path
        }
        if ("http" == schema) {
            return uri.toString()
        }
        if (ContentResolver.SCHEME_CONTENT == schema) {
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            var cursor: Cursor? = null
            var filePath: String? = ""
            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                if (cursor!!.moveToFirst()) {
                    filePath = cursor.getString(0)
                }
                cursor.close()
            } catch (e: Exception) { // do nothing
            } finally {
                try {
                    cursor?.close()
                } catch (e2: Exception) { // do nothing
                }
            }
            if (TextUtils.isEmpty(filePath)) {
                try {
                    val contentResolver = context.contentResolver
                    val selection = MediaStore.Images.Media._ID + "= ?"
                    var id = uri.lastPathSegment
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !TextUtils.isEmpty(id) && id!!.contains(":")) {
                        id = id.split(":").toTypedArray()[1]
                    }
                    val selectionArgs = arrayOf(id)
                    cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null)
                    if (cursor!!.moveToFirst()) {
                        filePath = cursor.getString(0)
                    }
                    cursor.close()
                } catch (e: Exception) { // do nothing
                } finally {
                    try {
                        cursor?.close()
                    } catch (e: Exception) { // do nothing
                    }
                }
            }
            return filePath
        }
        return null
    }

    const val MEDIA_TYPE_IMAGE = 1
    const val MEDIA_TYPE_VIDEO = 2
    /**
     * Create a File for saving an image or video
     */
    fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo")
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File
        mediaFile = if (type == MEDIA_TYPE_IMAGE) {
            File(mediaStorageDir.path + File.separator +
                    "IMG_" + timeStamp + ".jpg")
        } else if (type == MEDIA_TYPE_VIDEO) {
            File(mediaStorageDir.path + File.separator +
                    "VID_" + timeStamp + ".mp4")
        } else {
            return null
        }
        return mediaFile
    }

    private const val NUM_90 = 90
    private const val NUM_180 = 180
    private const val NUM_270 = 270
    fun rotateImage(bitmap: Bitmap, path: String?): Bitmap {
        var srcExif: ExifInterface? = null
        srcExif = try {
            ExifInterface(path)
        } catch (e: IOException) {
            e.printStackTrace()
            return bitmap
        }
        val matrix = Matrix()
        var angle = 0
        val orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> angle = NUM_90
            ExifInterface.ORIENTATION_ROTATE_180 -> angle = NUM_180
            ExifInterface.ORIENTATION_ROTATE_270 -> angle = NUM_270
            else -> {
            }
        }
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}