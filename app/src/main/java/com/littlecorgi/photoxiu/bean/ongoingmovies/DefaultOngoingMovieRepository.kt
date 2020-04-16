package com.littlecorgi.photoxiu.bean.ongoingmovies

import com.littlecorgi.photoxiu.Result
import com.littlecorgi.photoxiu.network.IFeedInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException


/**
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-12 16:10zx
 */
class DefaultOngoingMovieRepository : OngoingMovieRepository {

    /**
     * 通过Retrofit去网络获取数据
     *
     * @注 此方法实现为同步获取数据，所以调用此方法时最后在子线程或者IO协程中调用!!!
     */
    override suspend fun getOngoingMovies(): Result<OngoingMovies> {

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api-m.mtime.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val iFeedInterface = retrofit.create(IFeedInterface::class.java)
        val call = iFeedInterface.getMovie()
        return try {
            val response = call.execute()
            Result.Success(response.body()!!)
        } catch (e: ConnectException) {
            Result.Error(e)
        } catch (e: IOException) {
            Result.Error(e)
        }
    }

}