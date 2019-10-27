package com.littlecorgi.camera

import android.util.Size
import java.lang.Long.signum

internal class CompareSizeByArea : Comparator<Size> {
    override fun compare(o1: Size, o2: Size) = signum(o1.width.toLong() * o1.height - o2.width.toLong() * o2.height)
}