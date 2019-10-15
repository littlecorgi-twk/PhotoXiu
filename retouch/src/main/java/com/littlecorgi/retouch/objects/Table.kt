package com.littlecorgi.retouch.objects

import android.opengl.GLES20.GL_TRIANGLE_FAN
import android.opengl.GLES20.glDrawArrays
import com.littlecorgi.retouch.Constants.Companion.BYTES_PER_FLOAT
import com.littlecorgi.retouch.data.VertexArray
import com.littlecorgi.retouch.program.TextureShaderProgram

class Table {
    private var vertexArray: VertexArray

    init {
        vertexArray = VertexArray(VERTEX_DATA)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
        private const val STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT

        private val VERTEX_DATA: FloatArray = floatArrayOf(
                0F, 0F, 0.5F, 0.5F,
                -0.5F, -0.8F, 0F, 0.9F,
                0.5F, -0.8F, 1F, 0.9F,
                0.5F, 0.8F, 1F, 0.1F,
                -0.5F, 0.8F, 0F, 0.1F,
                -0.5F, -0.8F, 0F, 0.9F
        )
    }

    fun bindData(textureProgram: TextureShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        )

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
        )
    }

    fun draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
    }
}