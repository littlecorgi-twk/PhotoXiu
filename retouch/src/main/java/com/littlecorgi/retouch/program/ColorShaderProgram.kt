package com.littlecorgi.retouch.program

import android.content.Context
import android.opengl.GLES20.*
import com.littlecorgi.retouch.R

class ColorShaderProgram(context: Context): ShaderProgram(context, R.raw.retouch_simple_vertex_shader, R.raw.retouch_simple_fragment_shader) {
    private val uMatrixLocation = glGetUniformLocation(program, U_MATRIX)
    private val aPositionLocation = glGetAttribLocation(program, A_POSITION)
    private val aColorLocation = glGetAttribLocation(program, A_COLOR)

    public fun setUniforms(matrix: FloatArray) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

    public fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    public fun getColorAttributeLocation(): Int {
        return aColorLocation
    }
}

