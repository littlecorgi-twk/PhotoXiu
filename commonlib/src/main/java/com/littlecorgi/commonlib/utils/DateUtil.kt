package com.littlecorgi.commonlib.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author littlecorgi 2020/9/16
 */
object DateUtil {

    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @param format 格式
     * @return 返回字符串
     */
    @JvmStatic
    fun timeStamp2Date(seconds: String, format: String): String {
        var formatTemp = format
        if (seconds.isEmpty() || seconds == "null") {
            return ""
        }
        if (format.isEmpty()) {
            formatTemp = "yyyy-MM-dd HH:mm:ss"
        }
        val sfd = SimpleDateFormat(format, Locale.CHINA)
        return sfd.format(Date(seconds.toLong()))
    }
}