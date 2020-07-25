package com.littlecorgi.photoxiu.view.chooseframe

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.littlecorgi.commonlib.BaseActivity
import com.littlecorgi.photoxiu.MainActivity
import com.littlecorgi.photoxiu.R
import com.littlecorgi.photoxiu.ViewModelFactory
import com.littlecorgi.photoxiu.databinding.AppActivityChooseFrameBinding
import com.littlecorgi.photoxiu.viewModel.ChooseFrameViewModel
import java.util.concurrent.Executors

class ChooseFrameActivity : BaseActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var mBinding: AppActivityChooseFrameBinding
    private lateinit var mViewModel: ChooseFrameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.app_activity_choose_frame)
        mViewModel = ViewModelProvider(this, ViewModelFactory()).get(ChooseFrameViewModel::class.java)



        val singleExecutor = Executors.newSingleThreadExecutor()
        singleExecutor.execute {

        }
    }


    fun getVideoFrame(path: String?, time: Long): Bitmap? {
        val timeUs = time * 1000
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        return mmr.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
    }
}