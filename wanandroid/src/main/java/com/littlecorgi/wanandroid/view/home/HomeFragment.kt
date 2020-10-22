package com.littlecorgi.wanandroid.view.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.littlecorgi.wanandroid.R
import com.littlecorgi.wanandroid.databinding.WanandroidFragmentHomeBinding
import com.littlecorgi.wanandroid.view.MainViewModelFactory
import com.littlecorgi.wanandroid.view.adapter.HomeArticlesRecyclerViewAdapter
import com.littlecorgi.wanandroid.view.detailarticle.DetailArticleActivity

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
        private const val TAG = "HomeFragment"
    }

    private lateinit var mRvAdapter: HomeArticlesRecyclerViewAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mViewModel: HomeViewModel
    private lateinit var mBinding: WanandroidFragmentHomeBinding
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.wanandroid_fragment_home, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(activity!!, MainViewModelFactory()).get(HomeViewModel::class.java)
        mLayoutManager = LinearLayoutManager(mContext)
        mRvAdapter = HomeArticlesRecyclerViewAdapter(mViewModel.mDatas)
        initData()
        initRefresh()
        initRecyclerView()
    }

    private fun initRefresh() {
        mBinding.swipeFresh.setOnRefreshListener {
            mViewModel.getHomeList(0)
        }
    }

    private fun initRecyclerView() {
        mBinding.rvHomeArticle.apply {
            layoutManager = mLayoutManager
            mRvAdapter.setOnItemClickListener(object : HomeArticlesRecyclerViewAdapter.OnItemClickListener {
                override fun onClick(url: String, title: String) {
                    startActivity(Intent(mContext, DetailArticleActivity::class.java).apply {
                        putExtra("article_url", url)
                        putExtra("article_title", title)
                    })
                }
            })
            adapter = mRvAdapter
        }
    }

    private fun initData() {
        Log.d(TAG, "initData: 1")
        mViewModel.getHomeList.observe(viewLifecycleOwner, { result ->
            Log.d(TAG, "initData.observe: 1")
            val data = result.getOrNull()
            Log.d(TAG, "initData.observe: 2")
            if (data != null) {
                mBinding.rvHomeArticle.visibility = View.VISIBLE
                Log.d(TAG, "initData: ${data.datas}")
                // 添加数据
                mViewModel.mDatas.addAll(data.datas)
                mRvAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(mContext, "数据请求失败", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "initData: 数据请求失败")
                result.exceptionOrNull()?.printStackTrace()
            }
            mBinding.swipeFresh.isRefreshing = false
        })
        Log.d(TAG, "initData: 2")
        mViewModel.getHomeList(0)
        Log.d(TAG, "initData: 3")
    }
}