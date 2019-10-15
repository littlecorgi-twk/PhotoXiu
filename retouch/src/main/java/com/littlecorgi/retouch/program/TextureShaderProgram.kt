package com.littlecorgi.retouch.program

import android.content.Context
import android.opengl.GLES20.*
import com.littlecorgi.retouch.R

class TextureShaderProgram (context: Context) : ShaderProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader){
    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)
    private val uTextureUnitLocation: Int = glGetUniformLocation(program, U_TEXTURE_UNIT)
    private val aPositionLocation: Int = glGetAttribLocation(program, A_POSITION)
    private val aTextureCoordinatesLocation: Int = glGetAttribLocation(program, A_TEXTURE_COORDINATES)

    public fun setUniforms(matrix: FloatArray, textureId: Int) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        glActiveTexture(GL_TEXTURE0)

        glBindTexture(GL_TEXTURE_2D, textureId)

        glUniform1i(uTextureUnitLocation, 0)
    }

    public fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    public fun getTextureCoordinatesAttributeLocation(): Int {
        return aTextureCoordinatesLocation
    }
}