package com.littlecorgi.photoxiu.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.littlecorgi.photoxiu.R
import com.littlecorgi.photoxiu.databinding.AppFragmentModulesBinding

class ModulesFragment : Fragment() {

    companion object {
        fun newInstance() = ModulesFragment()
    }

    private lateinit var mActivity: FragmentActivity
    private lateinit var mViewModel: ModulesViewModel
    private lateinit var mBinding: AppFragmentModulesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.app_fragment_modules, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity = activity!!
        mViewModel = ViewModelProvider(mActivity).get(ModulesViewModel::class.java)
        initClick()
    }

    private fun initClick() {
        mBinding.btnToWanAndroid.setOnClickListener {
            ARouter.getInstance().build("/wanandroid/WanAndroidMainActivity").navigation()
        }
    }
}