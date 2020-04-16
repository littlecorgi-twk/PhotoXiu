package com.littlecorgi.photoxiu.bean.ongoingmovies

import com.littlecorgi.photoxiu.Result

/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-12 14:23
 */
interface OngoingMovieRepository {

    suspend fun getOngoingMovies(): Result<OngoingMovies>

}