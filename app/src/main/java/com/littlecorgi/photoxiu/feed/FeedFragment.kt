package com.littlecorgi.photoxiu.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import com.littlecorgi.photoxiu.R
import com.littlecorgi.photoxiu.ViewModelFactory
import com.littlecorgi.photoxiu.adapter.OngoingMovieRvAdapter
import com.littlecorgi.photoxiu.databinding.AppFragmentFeedBinding
import com.yc.pagerlib.recycler.PagerLayoutManager

class FeedFragment : Fragment() {

    companion object {
        private const val TAG = "FeedFragment"
        fun newInstance() = FeedFragment()
    }

    private lateinit var mActivity: FragmentActivity
    private lateinit var mViewModel: FeedViewModel
    private lateinit var mBinding: AppFragmentFeedBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.app_fragment_feed, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity = activity!!
        mViewModel = ViewModelProvider(mActivity, ViewModelFactory()).get(FeedViewModel::class.java)
        requestOngoingMovies()
    }

    private fun requestOngoingMovies() {
        mViewModel.requestMovies()
        initRecycler()
    }

    private fun initRecycler() {
        Log.d(TAG, "initRecycler: ${Thread.currentThread().name}")
        mBinding.rvFeed.layoutManager = PagerLayoutManager(
                activity, OrientationHelper.VERTICAL
        )
        val adapter = OngoingMovieRvAdapter(mActivity, ArrayList())
        mBinding.rvFeed.adapter = adapter
        mBinding.rvFeed.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                val jzvd: Jzvd = view.findViewById(R.id.ijkPlayer)
                // 当当前的item被滑出时释放资源
                if (Jzvd.CURRENT_JZVD != null && jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.currentUrl)) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos()
                    }
                }
            }

            override fun onChildViewAttachedToWindow(view: View) {
                val jzvd: Jzvd = view.findViewById(R.id.ijkPlayer)
                // 当滑动到当前item时自动播放
                jzvd.postDelayed({ jzvd.startVideo() }, 500)
            }
        })
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: OngoingMovieRvAdapter) {
        mViewModel.movies.observe(mActivity, androidx.lifecycle.Observer { movies ->
            adapter.items.clear()
            adapter.items.addAll(movies.ms!!)
            adapter.notifyDataSetChanged()
        })
        mViewModel.errorToastText.observe(mActivity, androidx.lifecycle.Observer { errorText ->
            Toast.makeText(mActivity, errorText, Toast.LENGTH_SHORT).show()
        })
    }

}