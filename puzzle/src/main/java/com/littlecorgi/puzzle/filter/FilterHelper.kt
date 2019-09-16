package com.littlecorgi.puzzle.filter

import android.graphics.*


class FilterHelper {
    companion object {

        private var mColorMatrix = FloatArray(20)

        fun grayScale(bitmap: Bitmap): Bitmap {
            mColorMatrix = floatArrayOf(
                    0.33F, 0.59F, 0.11F, 0F, 0F,
                    0.33F, 0.59F, 0.11F, 0F, 0F,
                    0.33F, 0.59F, 0.11F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
            )

            val bmp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val colorMatrix = ColorMatrix()
            colorMatrix.set(mColorMatrix)//将一维数组设置到ColorMatrix

            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(bitmap, 0F, 0F, paint)
            return bmp
        }

        fun negativeScale(bitmap: Bitmap): Bitmap {
            mColorMatrix = floatArrayOf(
                    -1F, 0F, 0F, 1F, 1F,
                    0F, -1F, 0F, 1F, 1F,
                    0F, 0F, -1F, 1F, 1F,
                    0F, 0F, 0F, 1F, 0F
            )

            val bmp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val colorMatrix = ColorMatrix()
            colorMatrix.set(mColorMatrix)//将一维数组设置到ColorMatrix

            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(bitmap, 0F, 0F, paint)
            return bmp
        }

        fun nostalgic(bitmap: Bitmap): Bitmap {
            mColorMatrix = floatArrayOf(
                    0.393F, 0.769F, 0.189F, 0F, 0F,
                    0.349F, 0.686F, 0.168F, 0F, 0F,
                    0.272F, 0.534F, 0.131F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
            )

            val bmp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val colorMatrix = ColorMatrix()
            colorMatrix.set(mColorMatrix)//将一维数组设置到ColorMatrix

            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(bitmap, 0F, 0F, paint)
            return bmp
        }

        fun removeColor(bitmap: Bitmap): Bitmap {
            mColorMatrix = floatArrayOf(
                    1.5F, 1.5F, 1.5F, 0F, -1F,
                    1.5F, 1.5F, 1.5F, 0F, -1F,
                    1.5F, 1.5F, 1.5F, 0F, -1F,
                    0F, 0F, 0F, 1F, 0F
            )

            val bmp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val colorMatrix = ColorMatrix()
            colorMatrix.set(mColorMatrix)//将一维数组设置到ColorMatrix

            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(bitmap, 0F, 0F, paint)
            return bmp
        }

        fun highSaturation(bitmap: Bitmap): Bitmap {
            mColorMatrix = floatArrayOf(
                    1.438F, -0.122F, -0.016F, 0F, -0.03F,
                    -0.062F, 1.378F, -0.016F, 0F, 0.05F,
                    -0.062F, -0.122F, 1.483F, 0F, -0.02F,
                    0F, 0F, 0F, 1F, 0F
            )

            val bmp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val colorMatrix = ColorMatrix()
            colorMatrix.set(mColorMatrix)//将一维数组设置到ColorMatrix

            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(bitmap, 0F, 0F, paint)
            return bmp
        }

        fun redGreenInverted(bitmap: Bitmap): Bitmap {
            mColorMatrix = floatArrayOf(
                    0F, 1F, 0F, 0F, 0F,
                    1F, 0F, 0F, 0F, 0F,
                    0F, 0F, 1F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
            )

            val bmp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val colorMatrix = ColorMatrix()
            colorMatrix.set(mColorMatrix)//将一维数组设置到ColorMatrix

            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(bitmap, 0F, 0F, paint)
            return bmp
        }


    }
}