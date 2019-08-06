package com.littlecorgi.puzzle.clipimage

import java.io.Closeable
import java.io.File
import java.io.IOException

class IOUtils {
    companion object {
        public fun close(c:Closeable) {
            try {
                c.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        public fun deleteFile(path: String): Boolean {
            val file = File(path)
            return file.exists() && file.isFile && file.delete()
        }
    }
}