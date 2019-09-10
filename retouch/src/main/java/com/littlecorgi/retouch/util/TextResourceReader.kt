package com.littlecorgi.retouch.util

import android.content.Context
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class TextResourceReader {

    companion object {
        public fun readTextFileFromResource(context: Context, resourceId: Int): String {
            val body = StringBuilder()

            val inputStream: InputStream = context.resources.openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferReader = BufferedReader(inputStreamReader)

            var nextLine: String?

            try {
                do {
                    nextLine = bufferReader.readLine()
                    if (nextLine != null) {
                        body.append(nextLine)
                        body.append('\n')
                    } else {
                        return body.toString()
                    }
                } while (true)
            } catch (e: IOException) {
                throw RuntimeException(
                        "Could not open resource$resourceId", e
                )
            } catch (nfe: Resources.NotFoundException) {
                throw RuntimeException("Resource not found: $resourceId", nfe)
            }
        }
    }
}