package com.littlecorgi.photoxiu.utils

import android.content.Context
import android.content.Intent

inline fun <reified T> Context.startActivity(block: Intent.() -> Intent?) {
    val intent = Intent(this, T::class.java)
    intent.block()
    startActivity(intent)
}