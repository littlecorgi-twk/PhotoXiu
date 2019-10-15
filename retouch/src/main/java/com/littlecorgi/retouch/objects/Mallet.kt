package com.littlecorgi.retouch.objects

import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import com.littlecorgi.retouch.Constants.Companion.BYTES_PER_FLOAT
import com.littlecorgi.retouch.data.VertexArray
import com.littlecorgi.retouch.program.ColorShaderProgram

class Mallet() {
    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
        private val VERTEX_DATA = floatArrayOf(
                0F, -0.4F, 0F, 0F, 1F,
                0F, 0.4F, 1F, 0F, 0F
        )
    }

    private val vertexArray: VertexArray

    init {
        vertexArray = VertexArray(VERTEX_DATA)
    }

    public fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        )
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                colorProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE
        )
    }

    public fun draw() {
        glDrawArrays(GL_POINTS, 0, 2)
    }

}