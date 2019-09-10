package com.littlecorgi.retouch

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.littlecorgi.retouch.util.LoggerConfig
import com.littlecorgi.retouch.util.ShaderHelper
import com.littlecorgi.retouch.util.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FirstOpenGLProjectRenderer() : GLSurfaceView.Renderer {

    private var vertexData: FloatBuffer? = null
    private var context: Context? = null
    private var program: Int = 0
    private var uColorLocation: Int = 0
    private var aPositionLocation: Int = 0

    companion object {
        const val POSITION_COMPONENT_COUNT: Int = 2
        const val BYTES_PER_FLOAT: Int = 4
        const val U_COLOR: String = "u_Color"
        const val A_POSITION = "a_Position"
    }

    constructor(_context: Context) : this() {
        context = _context
    }

    init {
        context = null
        vertexData = null

//        val tableVertices = floatArrayOf(
//                0F, 0F,
//                0F, 14F,
//                9F, 14F,
//                9F, 0F
//        )

//        val tableVerticesWithTriangles = floatArrayOf(
//                // Triangle 1
//                0F, 0F,
//                9F, 14F,
//                0F, 14F,
//                // Triangle 2
//                0F, 0F,
//                9F, 0F,
//                9F, 14F,
//
//                // Line 1
//                0F, 7F,
//                9F, 7F,
//
//                // Mallets
//                4.5F, 2F,
//                4.5F, 12F
//        )

        val tableVerticesWithTriangles = floatArrayOf(
                // Triangle 1
                -0.5F, -0.5F,
                0.5F, 0.5F,
                -0.5F, 0.5F,
                // Triangle 2
                -0.5F, -0.5F,
                0.5F, -0.5F,
                0.5F, 0.5F,

                // Line 1
                -0.5F, 0F,
                0.5F, 0F,

                // Mallets
                0F, -0.25F,
                0F, 0.25F,

                // ball
                0F, 0F
        )

        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()

        vertexData!!.put(tableVerticesWithTriangles)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0F, 0F, 0F, 0F)

        val vertexShaderSource = TextResourceReader.readTextFileFromResource(context!!, R.raw.simple_vertex_shader)
        val fragmentShaderSource = TextResourceReader.readTextFileFromResource(context!!, R.raw.simple_fragment_shader)

        // 编译着色器
        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)

        // 链接着色器
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader)

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program)
        }

        glUseProgram(program)

        // 获得uniform的位置
        uColorLocation = glGetUniformLocation(program, U_COLOR)
        // 获得属性的位置
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        vertexData!!.position(0)
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData)
        glEnableVertexAttribArray(aPositionLocation)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        // 画桌面
        glUniform4f(uColorLocation, 1.0F, 1.0F, 1.0F, 1.0F)
        glDrawArrays(GL_TRIANGLES, 0, 6)

        // 画中线
        glUniform4f(uColorLocation, 1.0F, 0.0F, 0.0F, 1.0F)
        glDrawArrays(GL_LINES, 6, 2)

        // 画第一个木槌
        glUniform4f(uColorLocation, 0.0F, 0.0F, 1.0F, 1.0F)
        glDrawArrays(GL_POINTS, 8, 1)

        // 画第二个木槌
        glUniform4f(uColorLocation, 0.0F, 0.0F, 1.0F, 1.0F)
        glDrawArrays(GL_POINTS, 9, 1)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }
}