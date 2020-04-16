package com.littlecorgi.photoxiu

import com.littlecorgi.photoxiu.Result.Success

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-13 11:22
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Success && data != null
