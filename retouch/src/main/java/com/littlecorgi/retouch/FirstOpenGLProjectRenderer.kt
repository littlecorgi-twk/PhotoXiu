package com.littlecorgi.retouch

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import com.littlecorgi.retouch.objects.Mallet
import com.littlecorgi.retouch.objects.Table
import com.littlecorgi.retouch.program.ColorShaderProgram
import com.littlecorgi.retouch.program.TextureShaderProgram
import com.littlecorgi.retouch.util.MatrixHelper
import com.littlecorgi.retouch.util.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FirstOpenGLProjectRenderer(private val context: Context) : GLSurfaceView.Renderer {

//     private var vertexData: FloatBuffer? = null
//     private var context: Context? = null
//     private var program: Int = 0
//     // private var uColorLocation: Int = 0
//     private var aPositionLocation: Int = 0
//     private var aColorLocation: Int = 0
//     private val projectionMatrix = FloatArray(16)
//     private var uMatrixLocation: Int = 0
//     private var modelMatrix = FloatArray(16)
//
//     companion object {
//         private const val POSITION_COMPONENT_COUNT: Int = 4
//         private const val BYTES_PER_FLOAT: Int = 4
//         // private const val U_COLOR: String = "u_Color"
//         private const val A_POSITION = "a_Position"
//         private const val A_COLOR = "a_Color"
//         private const val COLOR_COMPONENT_COUNT = 3
//         private const val STRIDE: Int = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
//         private const val U_MATRIX = "u_Matrix"
//     }
//
//     constructor(_context: Context) : this() {
//         context = _context
//     }
//
//     init {
//         context = null
//         vertexData = null
//
// //        val tableVertices = floatArrayOf(
// //                0F, 0F,
// //                0F, 14F,
// //                9F, 14F,
// //                9F, 0F
// //        )
//
// //        val tableVerticesWithTriangles = floatArrayOf(
// //                // Triangle 1
// //                0F, 0F,
// //                9F, 14F,
// //                0F, 14F,
// //                // Triangle 2
// //                0F, 0F,
// //                9F, 0F,
// //                9F, 14F,
// //
// //                // Line 1
// //                0F, 7F,
// //                9F, 7F,
// //
// //                // Mallets
// //                4.5F, 2F,
// //                4.5F, 12F
// //        )
//
//         val tableVerticesWithTriangles = floatArrayOf(
// //                // Triangle 1
// //                -0.5F, -0.5F,
// //                0.5F, 0.5F,
// //                -0.5F, 0.5F,
// //                // Triangle 2
// //                -0.5F, -0.5F,
// //                0.5F, -0.5F,
// //                0.5F, 0.5F,
//
//                 // Triangle Fan
//                 0F, 0F, 0F, 1.5F, 1F, 1F, 1F,
//                 -0.5F, -0.8F, 0F, 1F, 0.7F, 0.7F, 0.7F,
//                 0.5F, -0.8F, 0F, 1F, 0.7F, 0.7F, 0.7F,
//                 0.5F, 0.8F, 0F, 2F, 0.7F, 0.7F, 0.7F,
//                 -0.5F, 0.8F, 0F, 2F, 0.7F, 0.7F, 0.7F,
//                 -0.5F, -0.8F, 0F, 1F, 0.7F, 0.7F, 0.7F,
//
//                 // Line 1
//                 -0.5F, 0F, 0F, 1.5F, 1F, 0F, 0F,
//                 0.5F, 0F, 0F, 1.5F, 1F, 0F, 0F,
//
//                 // Mallets
//                 0F, -0.4F, 0F, 1.25F, 0F, 0F, 1F,
//                 0F, 0.4F, 0F, 1.25F, 1F, 0F, 0F
//         )
//
//         vertexData = ByteBuffer
//                 .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
//                 .order(ByteOrder.nativeOrder())
//                 .asFloatBuffer()
//
//         vertexData!!.put(tableVerticesWithTriangles)
//     }
//
//     override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//         glClearColor(0F, 0F, 0F, 0F)
//
//         val vertexShaderSource = TextResourceReader.readTextFileFromResource(context!!, R.raw.simple_vertex_shader)
//         val fragmentShaderSource = TextResourceReader.readTextFileFromResource(context!!, R.raw.simple_fragment_shader)
//
//         // 编译着色器
//         val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
//         val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)
//
//         // 链接着色器
//         program = ShaderHelper.linkProgram(vertexShader, fragmentShader)
//
//         if (LoggerConfig.ON) {
//             ShaderHelper.validateProgram(program)
//         }
//
//         glUseProgram(program)
//
//         // 获得uniform的位置
//         // uColorLocation = glGetUniformLocation(program, U_COLOR)
//         aColorLocation = glGetAttribLocation(program, A_COLOR)
//         // 获得属性的位置
//         aPositionLocation = glGetAttribLocation(program, A_POSITION)
//
//         vertexData!!.position(0)
//         // glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData)
//         glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData)
//         glEnableVertexAttribArray(aPositionLocation)
//
//         vertexData!!.position(POSITION_COMPONENT_COUNT)
//         glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData)
//         glEnableVertexAttribArray(aColorLocation)
//
//         uMatrixLocation = glGetUniformLocation(program, U_MATRIX)
//     }
//
//     override fun onDrawFrame(gl: GL10?) {
//         glClear(GL_COLOR_BUFFER_BIT)
//
//         glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)
//
//         // 画桌面
//         // 通过两个三角形
// //        glUniform4f(uColorLocation, 1.0F, 1.0F, 1.0F, 1.0F)
// //        glDrawArrays(GL_TRIANGLES, 0, 6)
//         // 通过三角形扇
//         // glUniform4f(uColorLocation, 1.0F, 1.0F, 1.0F, 1.0F)
//         glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
//
//         // 画中线
//         // glUniform4f(uColorLocation, 1.0F, 0.0F, 0.0F, 1.0F)
//         glDrawArrays(GL_LINES, 6, 2)
//
//         // 画第一个木槌
//         // glUniform4f(uColorLocation, 0.0F, 0.0F, 1.0F, 1.0F)
//         glDrawArrays(GL_POINTS, 8, 1)
//
//         // 画第二个木槌
//         // glUniform4f(uColorLocation, 0.0F, 0.0F, 1.0F, 1.0F)
//         glDrawArrays(GL_POINTS, 9, 1)
//     }
//
//     override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
//         glViewport(0, 0, width, height)
//
//         // val aspectRatio = if (width > height)
//         //     width.toFloat() / height.toFloat()
//         // else
//         //     height.toFloat() / width.toFloat()
//         //
//         // if (width > height) {
//         //     orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
//         // } else {
//         //     orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
//         // }
//
//         MatrixHelper.persectiveM(projectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f)
//         setIdentityM(modelMatrix, 0)
//         translateM(modelMatrix, 0, 0f, 0f, -3f)
//         rotateM(modelMatrix, 0, -40F, 1F, 0F, 0F)
//         val temp = FloatArray(16)
//         multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
//         System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)
//     }

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram
    private var texture: Int = 0

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        textureProgram.useProgram()
        textureProgram.setUniforms(projectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        colorProgram.useProgram()
        colorProgram.setUniforms(projectionMatrix)
        mallet.bindData(colorProgram)
        mallet.draw()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0F, 0.0F, 0.0F, 0.0F)

        table = Table()
        mallet = Mallet()

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.retouch_air_hockey_surface)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        MatrixHelper.persectiveM(projectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f)
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, 0f, 0f, -3f)
        rotateM(modelMatrix, 0, -40F, 1F, 0F, 0F)
        val temp = FloatArray(16)
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)
    }
}