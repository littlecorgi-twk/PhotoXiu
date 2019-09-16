package com.littlecorgi.puzzle.edit

import android.graphics.*

class ImageHelper {
    companion object {
        fun handleHueImageEffect(oldBmp: Bitmap, newBmp: Bitmap, hue: Float): Bitmap {
            val canvas = Canvas(newBmp)
            val paint = Paint()

            val hueMatrix = ColorMatrix()
            hueMatrix.setRotate(0, hue)
            hueMatrix.setRotate(1, hue)
            hueMatrix.setRotate(2, hue)

            val imageMatrix = ColorMatrix()
            imageMatrix.postConcat(hueMatrix)

            paint.colorFilter = ColorMatrixColorFilter(imageMatrix)
            canvas.drawBitmap(oldBmp, 0F, 0F, paint)

            return newBmp
        }

        fun handleSaturationImageEffect(oldBmp: Bitmap, newBmp: Bitmap, saturation: Float): Bitmap {
            val canvas = Canvas(newBmp)
            val paint = Paint()

            val saturationMatrix = ColorMatrix()
            saturationMatrix.setSaturation(saturation)

            val imageMatrix = ColorMatrix()
            imageMatrix.postConcat(saturationMatrix)

            paint.colorFilter = ColorMatrixColorFilter(imageMatrix)
            canvas.drawBitmap(oldBmp, 0F, 0F, paint)

            return newBmp
        }

        fun handleLumImageEffect(oldBmp: Bitmap, newBmp: Bitmap, lum: Float): Bitmap {
            val canvas = Canvas(newBmp)
            val paint = Paint()

            val lumMatrix = ColorMatrix()
            lumMatrix.setScale(lum, lum, lum, 1F)

            val imageMatrix = ColorMatrix()
            imageMatrix.postConcat(lumMatrix)

            paint.colorFilter = ColorMatrixColorFilter(imageMatrix)
            canvas.drawBitmap(oldBmp, 0F, 0F, paint)

            return newBmp
        }

        fun handleImageEffect(oldBmp: Bitmap, newBmp: Bitmap, hue: Float, saturation: Float, lum: Float): Bitmap {
            val canvas = Canvas(newBmp)
            val paint = Paint()

            val hueMatrix = ColorMatrix()
            hueMatrix.setRotate(0, hue)
            hueMatrix.setRotate(1, hue)
            hueMatrix.setRotate(2, hue)

            val saturationMatrix = ColorMatrix()
            saturationMatrix.setSaturation(saturation)

            val lumMatrix = ColorMatrix()
            lumMatrix.setScale(lum, lum, lum, 1F)

            val imageMatrix = ColorMatrix()
            imageMatrix.postConcat(hueMatrix)
            imageMatrix.postConcat(saturationMatrix)
            imageMatrix.postConcat(lumMatrix)

            paint.colorFilter = ColorMatrixColorFilter(imageMatrix)
            canvas.drawBitmap(oldBmp, 0F, 0F, paint)

            return newBmp
        }
    }
}