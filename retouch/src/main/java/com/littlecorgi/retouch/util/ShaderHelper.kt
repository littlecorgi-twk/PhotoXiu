package com.littlecorgi.retouch.util

import android.opengl.GLES20.*
import android.util.Log

class ShaderHelper {
    companion object {
        private const val TAG: String = "ShaderHelper"

        fun compileVertexShader(shaderCode: String): Int {
            return compileShader(GL_VERTEX_SHADER, shaderCode)
        }

        fun compileFragmentShader(shaderCode: String): Int {
            return compileShader(GL_FRAGMENT_SHADER, shaderCode)
        }

        private fun compileShader(type: Int, shaderCode: String): Int {

            // 创建一个新的着色器对象
            val shaderObjectId = glCreateShader(type)

            if (shaderObjectId == 0) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Could not create new shader")
                }
                return 0
            }

            // 上传和编译代码
            glShaderSource(shaderObjectId, shaderCode)
            glCompileShader(shaderObjectId)

            // 取出编译状态，检查是否成功编译
            val compileStatus = IntArray(1)
            glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)

            // 取出着色器信息日志
            if (LoggerConfig.ON) {
                Log.v(TAG, "Result of compiling source:\n" +
                        shaderCode +
                        "\n:" +
                        glGetShaderInfoLog(shaderObjectId))
            }

            // 验证编译状态
            if (compileStatus[0] == 0) {
                glDeleteShader(shaderObjectId)

                if (LoggerConfig.ON) {
                    Log.w(TAG, "Compilation of shade failed.")
                }

                return 0
            }
            // 返回着色器对象ID
            return shaderObjectId
        }

        fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
            // 新建程序对象并附上着色器

            val programObjectId: Int = glCreateProgram()

            if (programObjectId == 0) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Could not create new program")
                }
                return 0
            }

            // 附上着色器
            glAttachShader(programObjectId, vertexShaderId)
            glAttachShader(programObjectId, fragmentShaderId)

            // 链接程序
            glLinkProgram(programObjectId)

            // 输出日志
            val linkStatus = IntArray(1)
            glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)
            if (LoggerConfig.ON) {
                Log.v(TAG, "Result of linking program:\n${glGetProgramInfoLog(programObjectId)}")
            }

            // 验证链接状态
            if (linkStatus[0] == 0) {
                // If it filed, delete the program object
                glDeleteProgram(programObjectId)
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Linking of program failed.")
                }
                return 0
            }

            // 返回程序对象ID
            return programObjectId
        }

        /**
         * 验证OpenGL程序对象
         */
        fun validateProgram(programObjectId: Int): Boolean {
            glValidateProgram(programObjectId)

            val validateStatus = IntArray(1)
            glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
            Log.v(TAG, "Results of validating program: $validateStatus[0]\nLog: ${glGetProgramInfoLog(programObjectId)}")

            return validateStatus[0] != 0
        }
    }
}