package com.littlecorgi.photoxiu.adapter

//import com.littlecorgi.minidouyin.databinding.ItemRvFeedBinding

/**
 * {MainActivity}中用于feed流展示的RecyclerView的Adapter
 *
 * @author Tian Weikang tianweikang.corgi@bytedance.com
 * @date 2020-02-08 20-49
 */
//class RecyclerAdapter(private val mContext: Context) : BaseBindingAdapter<MsBean, ItemRvFeedBinding>(mContext) {
//    override fun getLayoutResId(viewType: Int): Int {
//        return R.layout.app_item_rv_feed
//    }
//
//    override fun onBindItem(binding: ItemRvFeedBinding?, item: MsBean?) {
//        binding!!.movieMs = item
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any?>) {
//        val binding: ItemRvFeedBinding? = DataBindingUtil.getBinding(holder.itemView)
//        binding!!.ijkPlayer.mVideoPlayerSurfaceView!!.mIjkPlayerController.setListener(VideoPlayerListener())
//        binding!!.ijkPlayer.load("http://lf1-hscdn-tos.pstatp.com/obj/developer-baas/baas/tt7217xbo2wz3cem41/a8efa55c5c22de69_1560563154288.mp4")
//        binding!!.ijkPlayer.visibility = View.VISIBLE
//        binding!!.ivFeed.visibility = View.GONE
//
//        onBindItem(binding, items[position])
//    }
//}