package com.littlecorgi.retouch.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils.texImage2D
import android.util.Log

class TextureHelper {
    companion object {
        private const val TAG = "TextureHelper"

        public fun loadTexture(context: Context, resourceID: Int): Int {
            val textureObjectIds: IntArray = IntArray(1)
            glGenTextures(1, textureObjectIds, 0)

            if (textureObjectIds[0] == 0) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Could not generate a new OpenGL texture object.")
                }
                return 0
            }

            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inScaled = false

            val bitmap: Bitmap = BitmapFactory.decodeResource(
                    context.resources, resourceID, options
            )

            if (bitmap == null) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Resource ID $resourceID could not be decoded.")
                }

                glDeleteTextures(1, textureObjectIds, 0)
                return 0
            }

            glBindTexture(GL_TEXTURE_2D, textureObjectIds[0])

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

            texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

            bitmap.recycle()
            glGenerateMipmap(GL_TEXTURE_2D)
            glBindTexture(GL_TEXTURE_2D, 0)
            return textureObjectIds[0]
        }
    }
}